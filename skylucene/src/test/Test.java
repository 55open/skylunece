package test;

public class Test{
       public static void main(String[] args)
       {
           UserDao dao = new UserDao();
       }
       /**
        * 
这是泛型擦拭法使得Generic无法获取自己的Generic Type类型。
实际上BadClass<String>()实例化以后Class里面就不包括T的信息了，
对于Class而言T已经被擦拭为Object，
<br>而真正的T参数被转到使用T的方法（或者变量声明或者其它使用T的地方）里面（如果没有那就没有存根），
所以无法反射到T的具体类别，
也就无法得到T.class。
而getGenericSuperclass()<br>
是Generic继承的特例，对于这种情况子类会保存父类的Generic参数类型，
返回一个ParameterizedType，
这时可以获取到父类的T.class了，这也正是子类确定应该继承什么T的方法。
        */
}