package com.alibaba.qlexpress4.example;

import com.alibaba.qlexpress4.Express4Runner;
import com.alibaba.qlexpress4.InitOptions;
import com.alibaba.qlexpress4.QLOptions;
import com.alibaba.qlexpress4.QLResult;
import com.alibaba.qlexpress4.annotation.QLAlias;
import com.alibaba.qlexpress4.security.QLSecurityStrategy;

/**
 * 演示 Express4Runner.executeWithAliasObjects() 方法的使用
 * 
 * 该方法允许使用 @QLAlias 注解来定义对象在脚本中的变量名
 */
public class AliasObjectExample {

    /**
     * 用户信息类 - 使用 @QLAlias 注解定义变量名为 "user"
     */
    @QLAlias("user")
    public static class UserInfo {
        @QLAlias("name")
        public String name;
        @QLAlias("age")
        public int age;
        @QLAlias("balance")
        public double balance;

        public UserInfo(String name, int age, double balance) {
            this.name = name;
            this.age = age;
            this.balance = balance;
        }
    }

    /**
     * 商品信息类 - 使用 @QLAlias 注解定义变量名为 "product"
     */
    @QLAlias("product")
    public static class ProductInfo {
        @QLAlias("productName")
        public String productName;
        @QLAlias("price")
        public double price;
        @QLAlias("stock")
        public int stock;

        public ProductInfo(String productName, double price, int stock) {
            this.productName = productName;
            this.price = price;
            this.stock = stock;
        }
    }

    /**
     * 配置信息类 - 使用 @QLAlias 注解定义变量名为 "config"
     */
    @QLAlias("config")
    public static class ConfigInfo {
        @QLAlias("discountRate")
        public double discountRate;
        @QLAlias("minPurchaseQuantity")
        public int minPurchaseQuantity;
        @QLAlias("enableVipDiscount")
        public boolean enableVipDiscount;

        public ConfigInfo(double discountRate, int minPurchaseQuantity, boolean enableVipDiscount) {
            this.discountRate = discountRate;
            this.minPurchaseQuantity = minPurchaseQuantity;
            this.enableVipDiscount = enableVipDiscount;
        }
    }

    /**
     * 没有注解的类 - 在脚本中不可访问
     */
    public static class InternalData {
        public String secretKey;

        public InternalData(String secretKey) {
            this.secretKey = secretKey;
        }
    }

    public static void main(String[] args) throws Exception {
        InitOptions initOptions = InitOptions.builder().securityStrategy(QLSecurityStrategy.open()).build();
        Express4Runner runner = new Express4Runner(initOptions);
        QLOptions options = QLOptions.DEFAULT_OPTIONS;

        System.out.println("=== 示例1: 单个对象使用 ===");
        singleObjectExample(runner, options);

        System.out.println("\n=== 示例2: 多个对象协同使用 ===");
        multipleObjectsExample(runner, options);

        System.out.println("\n=== 示例3: 复杂业务逻辑 ===");
        complexBusinessLogic(runner, options);

        System.out.println("\n=== 示例4: 忽略未注解对象 ===");
        ignoringNonAnnotatedObjects(runner, options);
    }

    /**
     * 示例1: 单个对象使用
     * 演示如何使用单个带 @QLAlias 注解的对象
     */
    private static void singleObjectExample(Express4Runner runner, QLOptions options) throws Exception {
        UserInfo userInfo = new UserInfo("张三", 28, 5000.0);

        // 在脚本中通过注解定义的名称 "user" 访问对象
        String script = "user.name + '的余额是' + user.balance + '元'";
        QLResult result = runner.executeWithAliasObjects(script, options, userInfo);
        System.out.println("结果: " + result.getResult()); // 输出: 张三的余额是5000.0元
    }

    /**
     * 示例2: 多个对象协同使用
     * 演示如何同时使用多个带注解的对象
     */
    private static void multipleObjectsExample(Express4Runner runner, QLOptions options) throws Exception {
        UserInfo userInfo = new UserInfo("李四", 35, 8000.0);
        ProductInfo productInfo = new ProductInfo("iPhone 15", 6999.0, 50);

        // 同时使用 user 和 product 两个对象
        String script = "if (user.balance >= product.price) { " +
                       "  return user.name + '可以购买' + product.productName; " +
                       "} else { " +
                       "  return user.name + '余额不足，还差' + (product.price - user.balance) + '元'; " +
                       "}";
        
        QLResult result = runner.executeWithAliasObjects(script, options, userInfo, productInfo);
        System.out.println("结果: " + result.getResult());
        // 输出: 李四可以购买iPhone 15
    }

    /**
     * 示例3: 复杂业务逻辑
     * 演示使用三个对象实现复杂的业务规则
     */
    private static void complexBusinessLogic(Express4Runner runner, QLOptions options) throws Exception {
        UserInfo userInfo = new UserInfo("王五", 30, 10000.0);
        ProductInfo productInfo = new ProductInfo("MacBook Pro", 12999.0, 20);
        ConfigInfo configInfo = new ConfigInfo(0.1, 2, true);

        // 复杂的购买逻辑：考虑库存、余额、折扣等
        String script = 
            "quantity = 2; " +
            "if (product.stock < quantity) { " +
            "  return '库存不足，仅剩' + product.stock + '件'; " +
            "}; " +  // 注意这里添加了分号
            "totalPrice = product.price * quantity; " +
            "if (config.enableVipDiscount && quantity >= config.minPurchaseQuantity) { " +
            "  totalPrice = totalPrice * (1 - config.discountRate); " +
            "}; " +  // 注意这里添加了分号
            "if (user.balance >= totalPrice) { " +
            "  return user.name + '购买成功！应付' + totalPrice + '元（已享受' + (config.discountRate * 100) + '%折扣）'; " +
            "} else { " +
            "  return '余额不足，还需' + (totalPrice - user.balance) + '元'; " +
            "}";

        QLResult result = runner.executeWithAliasObjects(script, options, userInfo, productInfo, configInfo);
        System.out.println("结果: " + result.getResult());
        // 输出: 王五购买成功！应付23398.2元（已享受10.0%折扣）
    }

    /**
     * 示例4: 忽略未注解对象
     * 演示没有 @QLAlias 注解的对象会被忽略
     */
    private static void ignoringNonAnnotatedObjects(Express4Runner runner, QLOptions options) throws Exception {
        UserInfo userInfo = new UserInfo("赵六", 25, 3000.0);
        InternalData internalData = new InternalData("secret-key-12345"); // 没有注解，会被忽略

        // 只能访问带注解的 user 对象，internal 对象不可访问
        String script = "user.name + '的年龄是' + user.age";
        QLResult result = runner.executeWithAliasObjects(script, options, userInfo, internalData);
        System.out.println("结果: " + result.getResult());
        // 输出: 赵六的年龄是25

        // 尝试访问未注解的对象会报错
        try {
            String errorScript = "interal.secretKey"; // internal 不存在
            QLResult qlResult = runner.executeWithAliasObjects(errorScript, options, userInfo, internalData);
            System.out.println("结果: " + qlResult.getResult());
        } catch (Exception e) {
            System.out.println("预期错误: 无法访问未注解的对象 - " + e.getMessage());
        }
    }
}
