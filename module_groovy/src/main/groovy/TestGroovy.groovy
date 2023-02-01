class TestGroovy {
    static void main(String[] args) {
        IFoo ifoo = GroovyHelper.invokeMethod('FooGroovy.groovy');
        println(ifoo.run(1))
    }
}