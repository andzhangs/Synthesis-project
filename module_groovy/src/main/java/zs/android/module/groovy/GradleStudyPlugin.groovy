package zs.android.module.groovy
//package zs.android.module.groovy.android
//
//
///**
// * 自定义Plugin
// */
//class zs.android.module.groovy.GradleStudyPlugin implements Plugin<Project> {
//
//    /**
//     * 唯一需要实现的就是这个方法，参数就是引入了当前插件的Project对象
//     * @param project
//     */
//    @Override
//    void apply(Project project) {
////        //创建扩展属性
//        project.extensions.create('imoocReleaseInfo', zs.android.module.groovy.ReleaseInfoExtension)
////        //创建Task
//        project.tasks.create('imoocReleaseInfoTask', zs.android.module.groovy.ReleaseInfoTask)
//
//        println '打印---->>>>>>>>：'+project.name
//    }
//}