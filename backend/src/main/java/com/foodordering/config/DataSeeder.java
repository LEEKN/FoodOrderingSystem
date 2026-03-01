package com.foodordering.config;

import com.foodordering.repository.OrderRepository;
import com.foodordering.model.entity.Coupon;
import com.foodordering.model.entity.Menu;
import com.foodordering.model.enums.DiscountType;
import com.foodordering.repository.CouponRepository;
import com.foodordering.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public void run(String... args) throws Exception {

        // 為了確保載入最新且完整的菜單，如果資料庫餐點數量少於 10 個，就清空並重新建立
        if (menuRepository.count() < 10) {
            orderRepository.deleteAll();

            menuRepository.deleteAll();
            System.out.println("⏳ 開始建立完整菜單...");

            List<Menu> menus = Arrays.asList(
                    // --- 漢堡系列 (Burgers) ---
                    createMenu("經典經典起司堡", "100% 安格斯牛肉、切達起司、醃黃瓜、洋蔥、特製漢堡醬。", 180, "Burgers"),
                    createMenu("培根煙燻燒烤堡", "安格斯牛肉、酥脆培根、切達起司、脆洋蔥絲、BBQ 燒烤醬。", 220, "Burgers"),
                    createMenu("雙層重磅肉彈堡", "兩片安格斯牛肉餅、雙倍切達起司、焦糖炒洋蔥。", 280, "Burgers"),
                    createMenu("松露野蕈牛排堡", "安格斯牛肉、綜合炒菇、黑松露起司醬、芝麻葉。", 260, "Burgers"),
                    createMenu("酪梨莎莎牛肉堡", "安格斯牛肉、新鮮酪梨片、墨西哥番茄辣醬、傑克起司。", 250, "Burgers"),
                    createMenu("熔岩花生培根堡", "安格斯牛肉、顆粒花生醬、培根、酥脆薯餅。", 240, "Burgers"),
                    createMenu("南方風味炸雞堡", "秘製酪乳炸雞腿排、酸黃瓜、高麗菜絲沙拉、蜂蜜芥末醬。", 200, "Burgers"),
                    createMenu("水牛城辣雞堡", "酥脆炸雞腿排、水牛城辣醬、藍起司醬、芹菜絲。", 210, "Burgers"),
                    createMenu("深海酥炸鱈魚堡", "金黃鱈魚排、塔塔醬、酸豆、新鮮生菜。", 190, "Burgers"),
                    createMenu("未來植感素食堡", "植物肉排、純素起司、新鮮番茄、生菜、全麥漢堡麵包。", 220, "Burgers"),

                    // --- 副食系列 (Sides) ---
                    createMenu("原味粗薯條", "經典美式直切薯條，撒上粗海鹽。", 60, "Sides"),
                    createMenu("起司肉醬薯條", "粗薯條淋上特製波隆那肉醬與融化起司。", 100, "Sides"),
                    createMenu("松露帕瑪森薯條", "粗薯條拌入松露油與現刨帕瑪森起司碎。", 120, "Sides"),
                    createMenu("金黃洋蔥圈", "新鮮洋蔥切片裹上啤酒麵衣炸至酥脆。", 80, "Sides"),
                    createMenu("紐奧良炸雞翅", "辛香料醃漬雞翅，搭配藍起司蘸醬。", 160, "Sides"),
                    createMenu("莫札瑞拉起司條", "外皮酥脆，內餡為牽絲莫札瑞拉起司。", 110, "Sides"),
                    createMenu("奶油玉米濃湯", "美式經典濃郁風味，含甜玉米粒與培根碎。", 70, "Sides"),
                    createMenu("田園鮮蔬沙拉", "綜合綠葉蔬菜、小番茄、小黃瓜、搭配義式油醋醬。", 120, "Sides"),
                    createMenu("酥脆雞米花", "一口大小的去骨雞腿肉，配塔塔醬。", 90, "Sides"),
                    createMenu("墨西哥辣椒球", "切碎的墨西哥辣椒與起司拌勻後油炸。", 130, "Sides"),
                    createMenu("蒜味香草地瓜條", "甜地瓜條搭配新鮮蒜末與香草。", 80, "Sides"),

                    // --- 飲品系列 (Drinks) ---
                    createMenu("經典可口可樂", "冰鎮可樂。", 40, "Drinks"),
                    createMenu("零卡可樂", "無糖無負擔的氣泡選擇。", 40, "Drinks"),
                    createMenu("雪碧", "清爽檸檬萊姆氣泡飲。", 40, "Drinks"),
                    createMenu("經典巧克力奶昔", "濃郁香草冰淇淋與純可可粉調製。", 120, "Drinks"),
                    createMenu("草莓優格奶昔", "新鮮草莓果醬與香草冰淇淋拌勻。", 120, "Drinks"),
                    createMenu("美式手作冰紅茶", "每日現煮紅茶，可調整甜度。", 50, "Drinks"),
                    createMenu("現榨檸檬汽水", "鮮榨檸檬汁加入氣泡水與楓糖。", 80, "Drinks")
            );
            menuRepository.saveAll(menus);
            System.out.println("✅ 完整菜單建立完成！共 " + menus.size() + " 項餐點。");
        }

        // 建立完整優惠券
        if (couponRepository.count() < 10) {
            couponRepository.deleteAll();
            System.out.println("⏳ 開始建立優惠券...");

            List<Coupon> coupons = Arrays.asList(
                    createCoupon("OFF95", "95折", DiscountType.PERCENTAGE, "0.95"),
                    createCoupon("OFF90", "9折", DiscountType.PERCENTAGE, "0.90"),
                    createCoupon("OFF85", "85折", DiscountType.PERCENTAGE, "0.85"),
                    createCoupon("OFF80", "8折", DiscountType.PERCENTAGE, "0.80"),
                    createCoupon("OFF75", "75折", DiscountType.PERCENTAGE, "0.75"),
                    createCoupon("OFF70", "7折", DiscountType.PERCENTAGE, "0.70"),
                    createCoupon("MINUS10", "折抵 10 元", DiscountType.FIXED_AMOUNT, "10"),
                    createCoupon("MINUS15", "折抵 15 元", DiscountType.FIXED_AMOUNT, "15"),
                    createCoupon("MINUS20", "折抵 20 元", DiscountType.FIXED_AMOUNT, "20"),
                    createCoupon("MINUS30", "折抵 30 元", DiscountType.FIXED_AMOUNT, "30"),
                    createCoupon("MINUS50", "折抵 50 元", DiscountType.FIXED_AMOUNT, "50"),
                    createCoupon("MINUS100", "折抵 100 元", DiscountType.FIXED_AMOUNT, "100")
            );
            couponRepository.saveAll(coupons);
            System.out.println("✅ 優惠券建立完成！共 " + coupons.size() + " 張。");
        }
    }

    // --- 輔助方法：快速建立餐點 ---
    private Menu createMenu(String name, String description, int price, String category) {
        Menu m = new Menu();
        m.setName(name);
        m.setDescription(description);
        m.setPrice(new BigDecimal(price));
        m.setCategory(category);
        m.setAvailable(true);
        return m;
    }

    // --- 輔助方法：快速建立優惠券 ---
    private Coupon createCoupon(String code, String description, DiscountType type, String value) {
        Coupon c = new Coupon();
        c.setCode(code);
        c.setDescription(description);
        c.setDiscountType(type);
        c.setDiscountValue(new BigDecimal(value));
        c.setMinOrderAmount(BigDecimal.ZERO); // 預設無低消，方便測試
        c.setActive(true);
        return c;
    }
}