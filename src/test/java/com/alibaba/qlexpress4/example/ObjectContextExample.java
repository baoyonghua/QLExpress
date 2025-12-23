package com.alibaba.qlexpress4.example;

import com.alibaba.qlexpress4.Express4Runner;
import com.alibaba.qlexpress4.InitOptions;
import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.QLResult;

/**
 * 演示 Express4Runner.execute(String script, Object context, QLOptions qlOptions) 方法的使用
 * 
 * 该方法允许将一个普通 Java 对象的字段作为脚本变量使用
 */
public class ObjectContextExample {

    /**
     * 定义一个用户上下文对象
     * 该对象的公共字段会自动暴露给脚本
     */
    public static class UserContext {
        public String name;
        public int age;
        public double salary;
        public boolean isVip;

        public UserContext(String name, int age, double salary, boolean isVip) {
            this.name = name;
            this.age = age;
            this.salary = salary;
            this.isVip = isVip;
        }
    }

    /**
     * 定义一个订单上下文对象
     */
    public static class OrderContext {

        public static final String name = "订单";

        public double totalAmount;
        public int quantity;
        public String productName;
        public double discount;

        public double getTotalAmount() {
            return totalAmount;
        }


        public OrderContext(double totalAmount, int quantity, String productName, double discount) {
            this.totalAmount = totalAmount;
            this.quantity = quantity;
            this.productName = productName;
            this.discount = discount;
        }
    }

    public static void main(String[] args) throws Exception {
        Express4Runner runner = new Express4Runner(InitOptions.DEFAULT_OPTIONS);
        QLOptions options = QLOptions.DEFAULT_OPTIONS;

        System.out.println("=== 示例1: 基本字段访问 ===");
        basicFieldAccess(runner, options);

        System.out.println("\n=== 示例2: 条件判断 ===");
        conditionalLogic(runner, options);

        System.out.println("\n=== 示例3: 复杂计算 ===");
        complexCalculation(runner, options);

        System.out.println("\n=== 示例4: 字符串操作 ===");
        stringOperation(runner, options);
    }

    /**
     * 示例1: 基本字段访问
     * 演示如何在脚本中直接使用对象的字段
     */
    private static void basicFieldAccess(Express4Runner runner, QLOptions options) throws Exception {
        UserContext user = new UserContext("张三", 25, 8000.0, true);

        // 脚本中直接使用字段名作为变量
        String script = "name + '的年龄是' + age";
        QLResult result = runner.execute(script, user, options);
        System.out.println("结果: " + result.getResult()); // 输出: 张三的年龄是25
    }

    /**
     * 示例2: 条件判断
     * 演示在脚本中使用对象字段进行条件判断
     */
    private static void conditionalLogic(Express4Runner runner, QLOptions options) throws Exception {
        UserContext user = new UserContext("李四", 30, 12000.0, true);

        // 根据字段值进行条件判断
        String script = "if (isVip && salary > 10000) { return '高级VIP会员'; } else { return '普通会员'; }";
        QLResult result = runner.execute(script, user, options);
        System.out.println("会员等级: " + result.getResult()); // 输出: 高级VIP会员
    }

    /**
     * 示例3: 复杂计算
     * 演示使用多个字段进行复杂计算
     */
    private static void complexCalculation(Express4Runner runner, QLOptions options) throws Exception {
        OrderContext order = new OrderContext(1000.0, 5, "笔记本电脑", 0.1);

        // 计算最终价格: 总额 * (1 - 折扣)
        String script = "name + '价格 = ' + totalAmount * (1 - discount)";
        QLResult result = runner.execute(script, order, options);
        System.out.println("最终价格: " + result.getResult()); // 输出: 900.0

        // 计算单价
        script = "totalAmount / quantity";
        result = runner.execute(script, order, options);
        System.out.println("单价: " + result.getResult()); // 输出: 200.0
    }

    /**
     * 示例4: 字符串操作
     * 演示字符串拼接和处理
     */
    private static void stringOperation(Express4Runner runner, QLOptions options) throws Exception {
        OrderContext order = new OrderContext(1500.0, 3, "机械键盘", 0.15);

        // 生成订单描述
        String script = "'购买了' + quantity + '个' + productName + '，优惠' + (discount * 100) + '%'";
        QLResult result = runner.execute(script, order, options);
        System.out.println("订单描述: " + result.getResult()); 
        // 输出: 购买了3个机械键盘，优惠15.0%
    }
}
