package zs.android.module.nearby

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.AdvertisingOptions
import com.google.android.gms.nearby.connection.ConnectionInfo
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback
import com.google.android.gms.nearby.connection.ConnectionResolution
import com.google.android.gms.nearby.connection.ConnectionsClient
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo
import com.google.android.gms.nearby.connection.DiscoveryOptions
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback
import com.google.android.gms.nearby.connection.Payload
import com.google.android.gms.nearby.connection.PayloadCallback
import com.google.android.gms.nearby.connection.PayloadTransferUpdate
import com.google.android.gms.nearby.connection.Strategy
import zs.android.module.nearby.databinding.ActivityMainBinding
import java.util.Random
import kotlin.text.Charsets.UTF_8

/**
 * https://developers.google.cn/nearby/messages/android/get-started?hl=zh-cn
 * https://developer.android.google.cn/codelabs/nearby-connections?hl=zh_cn#0
 */
class MainActivity : AppCompatActivity() {

    /**
     * Enum class for defining the winning rules for Rock-Paper-Scissors. Each player will make a
     * choice, then the beats function in this class will be used to determine whom to reward the
     * point to.
     */
    private enum class GameChoice {
        ROCK, PAPER, SCISSORS;

        fun beats(other: GameChoice): Boolean =
            (this == ROCK && other == SCISSORS)
                    || (this == SCISSORS && other == PAPER)
                    || (this == PAPER && other == ROCK)
    }

    /**
     * Instead of having each player enter a name, in this sample we will conveniently generate
     * random human readable names for players.
     */
    internal object CodenameGenerator {
        private val COLORS = arrayOf(
            "Red", "Orange", "Yellow", "Green", "Blue", "Indigo", "Violet", "Purple", "Lavender"
        )
        private val TREATS = arrayOf(
            "Cupcake", "Donut", "Eclair", "Froyo", "Gingerbread", "Honeycomb",
            "Ice Cream Sandwich", "Jellybean", "Kit Kat", "Lollipop", "Marshmallow", "Nougat",
            "Oreo", "Pie"
        )
        private val generator = Random()

        /** Generate a random Android agent codename  */
        fun generate(): String {
            val color = COLORS[generator.nextInt(COLORS.size)]
            val treat = TREATS[generator.nextInt(TREATS.size)]
            return "$color $treat"
        }
    }

    /**
     * Strategy for telling the Nearby Connections API how we want to discover and connect to
     * other nearby devices. A star shaped strategy means we want to discover multiple devices but
     * only connect to and communicate with one at a time.
     */
    private val STRATEGY = Strategy.P2P_STAR

    /**
     * Our handle to the [Nearby Connections API][ConnectionsClient].
     */
    private lateinit var mConnectionsClient: ConnectionsClient

    /**
     * The request code for verifying our call to [requestPermissions]. Recall that calling
     * [requestPermissions] leads to a callback to [onRequestPermissionsResult]
     */
    private val REQUEST_CODE_REQUIRED_PERMISSIONS = 1

    /*
    The following variables are convenient ways of tracking the data of the opponent that we
    choose to play against.
    */
    private var opponentName: String? = null
    private var opponentEndpointId: String? = null
    private var opponentScore = 0
    private var opponentChoice: GameChoice? = null

    /*
    The following variables are for tracking our own data
    */
    private var myCodeName: String = CodenameGenerator.generate()
    private var myScore = 0
    private var myChoice: GameChoice? = null

    /**
     * This is for wiring and interacting with the UI views.
     */
    private lateinit var mDataBinding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mConnectionsClient = Nearby.getConnectionsClient(this)

        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
//                val mm=Nearby.getMessagesClient(this, MessagesOptions.Builder().setPermissions(NearbyPermissions.BLE).build())
//                recreate()
            }
        }.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        mDataBinding.myName.text = "You\n($myCodeName)"
        mDataBinding.findOpponent.setOnClickListener {
            startAdvertising()
            startDiscovery()
            mDataBinding.status.text = "Searching for opponents..."
            // "find opponents" is the opposite of "disconnect" so they don't both need to be
            // visible at the same time
            mDataBinding.findOpponent.visibility = View.GONE
            mDataBinding.disconnect.visibility = View.VISIBLE
        }
        // wire the controller buttons
        mDataBinding.apply {
            rock.setOnClickListener { sendGameChoice(GameChoice.ROCK) }
            paper.setOnClickListener { sendGameChoice(GameChoice.PAPER) }
            scissors.setOnClickListener { sendGameChoice(GameChoice.SCISSORS) }
        }
        mDataBinding.disconnect.setOnClickListener {
            opponentEndpointId?.let { mConnectionsClient.disconnectFromEndpoint(it) }
            resetGame()
        }

        resetGame() // we are about to start a new game
    }

    /** Sends the user's selection of rock, paper, or scissors to the opponent. */
    private fun sendGameChoice(choice: GameChoice) {
        myChoice = choice
        mConnectionsClient.sendPayload(
            opponentEndpointId!!,
            Payload.fromBytes(choice.name.toByteArray(UTF_8))
        )
        mDataBinding.status.text = "You chose ${choice.name}"
        // For fair play, we will disable the game controller so that users don't change their
        // choice in the middle of a game.
        setGameControllerEnabled(false)
    }

    /**
     * Enables/Disables the rock, paper and scissors buttons. Disabling the game controller
     * prevents users from changing their minds after making a choice.
     */
    private fun setGameControllerEnabled(state: Boolean) {
        mDataBinding.apply {
            rock.isEnabled = state
            paper.isEnabled = state
            scissors.isEnabled = state
        }
    }

    /** callback for receiving payloads */
    private val payloadCallback: PayloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            payload.asBytes()?.let {
                opponentChoice = GameChoice.valueOf(String(it, UTF_8))
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
            // Determines the winner and updates game state/UI after both players have chosen.
            // Feel free to refactor and extract this code into a different method
            if (update.status == PayloadTransferUpdate.Status.SUCCESS
                && myChoice != null && opponentChoice != null
            ) {
                val mc = myChoice!!
                val oc = opponentChoice!!
                when {
                    mc.beats(oc) -> { // Win!
                        mDataBinding.status.text = "${mc.name} beats ${oc.name}"
                        myScore++
                    }

                    mc == oc -> { // Tie
                        mDataBinding.status.text = "You both chose ${mc.name}"
                    }

                    else -> { // Loss
                        mDataBinding.status.text = "${mc.name} loses to ${oc.name}"
                        opponentScore++
                    }
                }
                mDataBinding.score.text = "$myScore : $opponentScore"
                myChoice = null
                opponentChoice = null
                setGameControllerEnabled(true)
            }
        }
    }

    // Callbacks for connections to other devices
    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            // Accepting a connection means you want to receive messages. Hence, the API expects
            // that you attach a PayloadCall to the acceptance
            mConnectionsClient.acceptConnection(endpointId, payloadCallback)
            opponentName = "Opponent\n(${info.endpointName})"
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            if (result.status.isSuccess) {
                mConnectionsClient.stopAdvertising()
                mConnectionsClient.stopDiscovery()
                opponentEndpointId = endpointId
                mDataBinding.opponentName.text = opponentName
                mDataBinding.status.text = "Connected"
                setGameControllerEnabled(true) // we can start playing
            }
        }

        override fun onDisconnected(endpointId: String) {
            resetGame()
        }
    }

    /** Wipes all game state and updates the UI accordingly. */
    private fun resetGame() {
        // reset data
        opponentEndpointId = null
        opponentName = null
        opponentChoice = null
        opponentScore = 0
        myChoice = null
        myScore = 0
        // reset state of views
        mDataBinding.disconnect.visibility = View.GONE
        mDataBinding.findOpponent.visibility = View.VISIBLE
        setGameControllerEnabled(false)
        mDataBinding.opponentName.text = "opponent\n(none yet)"
        mDataBinding.status.text = "..."
        mDataBinding.score.text = ":"
    }

    private fun startAdvertising() {
        val options = AdvertisingOptions.Builder().setStrategy(STRATEGY).build()
        // Note: Advertising may fail. To keep this demo simple, we don't handle failures.
        mConnectionsClient.startAdvertising(
            myCodeName,
            packageName,
            connectionLifecycleCallback,
            options
        )
    }

    // Callbacks for finding other devices
    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            mConnectionsClient.requestConnection(myCodeName, endpointId, connectionLifecycleCallback)
        }

        override fun onEndpointLost(endpointId: String) {
        }
    }

    private fun startDiscovery() {
        val options = DiscoveryOptions.Builder().setStrategy(STRATEGY).build()
        mConnectionsClient.startDiscovery(packageName, endpointDiscoveryCallback, options)
    }

    override fun onStop() {
        mConnectionsClient.apply {
            stopAdvertising()
            stopDiscovery()
            stopAllEndpoints()
        }
        resetGame()
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}