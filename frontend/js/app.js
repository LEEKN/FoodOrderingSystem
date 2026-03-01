// frontend/js/app.js (或直接寫在 script 標籤)
const API_BASE_URL = "http://localhost:8080/api";

let allMenus = []; // 用來存放後端回傳的所有餐點

async function fetchMenus() {
    try {
        const response = await fetch(`${API_BASE_URL}/menus`);
        if (!response.ok) throw new Error("網路請求失敗");

        allMenus = await response.json(); // 👈 存入全域變數
        renderMenus(allMenus); // 一開始顯示全部
    } catch (error) {
        console.error("無法取得菜單:", error);
        document.getElementById('menu-container').innerHTML =
            `<p class="text-red-500 text-center py-20">無法載入菜單，請檢查後端是否啟動。</p>`;
    }
}

// 👇 分類過濾函式
function filterCategory(category) {
    // 1. 處理按鈕樣式切換
    const buttons = document.querySelectorAll('.cat-btn');
    buttons.forEach(btn => {
        btn.classList.remove('bg-yellow-500', 'text-black');
        btn.classList.add('bg-stone-800', 'text-white');
    });

    // 讓被點擊的按鈕變成黃色
    const activeBtn = document.getElementById(`btn-${category}`);
    if (activeBtn) {
        activeBtn.classList.remove('bg-stone-800', 'text-white');
        activeBtn.classList.add('bg-yellow-500', 'text-black');
    }

    // 2. 過濾資料
    if (category === 'All') {
        renderMenus(allMenus);
    } else {
        const filtered = allMenus.filter(item => item.category === category);
        renderMenus(filtered);
    }
}

// 3. 更新渲染邏輯 (讓所有餐點的按鈕統一呼叫 showItemModal)
function renderMenus(menus) {
    const container = document.getElementById('menu-container');

    if (menus.length === 0) {
        container.innerHTML = `<p class="col-span-full text-center py-10 text-stone-500">此分類目前沒有餐點喔！</p>`;
        return;
    }

    container.innerHTML = menus.map(item => `
        <div class="bg-stone-800 p-4 rounded-xl border border-stone-700 hover:border-yellow-500 transition-all group flex flex-col justify-between">
            <div>
                <div class="h-40 bg-stone-700 rounded-lg mb-4 flex items-center justify-center text-stone-500 overflow-hidden">
                    ${item.imageUrl ? `<img src="${item.imageUrl}" class="w-full h-full object-cover">` : `<span class="italic">Rocket Burger</span>`}
                </div>
                <h3 class="text-xl font-bold">${item.name}</h3>
                <span class="text-xs bg-stone-900 text-stone-400 px-2 py-1 rounded inline-block mt-1">${item.category}</span>
                <p class="text-stone-400 text-sm mt-2 h-10 overflow-hidden">${item.description || '美味推薦'}</p>
                <p class="text-yellow-500 text-2xl font-black mt-2">$${item.price}</p>
            </div>
            <button onclick="showItemModal(${item.id})" class="w-full mt-4 bg-red-600 hover:bg-red-500 py-2 rounded-lg font-bold transition-colors">
                選擇餐點
            </button>
        </div>
    `).join('');
}

// ==========================================
// 👇 新增：數量加減與限制邏輯 👇
// ==========================================
function changeQuantity(delta) {
    const input = document.getElementById('modal-quantity');
    let val = parseInt(input.value) || 1;
    val += delta;
    if (val < 1) val = 1;
    if (val > 99) val = 99;
    input.value = val;
}

function limitQuantity(input) {
    let val = parseInt(input.value);
    if (val < 1) input.value = 1;
    if (val > 99) input.value = 99;
}

// ==========================================
// 👇 修正：智慧型商品彈窗邏輯 👇
// ==========================================
function showItemModal(itemId) {
    const item = allMenus.find(m => m.id === itemId);

    // 1. 填充餐點文字資料
    document.getElementById('modal-item-name').innerText = item.name;
    document.getElementById('modal-item-desc').innerText = item.description || '無詳細描述';
    document.getElementById('modal-item-price').innerText = `$${item.price}`;

    // 2. 填充圖片 (如果未來後端有圖片網址的話)
    const imgEl = document.getElementById('modal-image');
    const noImgEl = document.getElementById('modal-no-image');
    if (item.imageUrl) {
        imgEl.src = item.imageUrl;
        imgEl.classList.remove('hidden');
        noImgEl.classList.add('hidden');
    } else {
        imgEl.classList.add('hidden');
        noImgEl.classList.remove('hidden');
    }

    // 3. 每次打開彈窗，預設數量歸 1
    document.getElementById('modal-quantity').value = 1;

    // 4. 判斷類別，顯示對應的區塊與按鈕
    const isBurger = item.category === 'Burgers';
    const comboOptions = document.getElementById('combo-options');
    const btnAddCart = document.getElementById('btn-add-cart');
    const btnAlacarte = document.getElementById('btn-alacarte');
    const btnCombo = document.getElementById('btn-combo');

    if (isBurger) {
        // 是漢堡：顯示套餐選項與 單點/升級 按鈕
        comboOptions.classList.remove('hidden');
        btnAlacarte.classList.remove('hidden');
        btnCombo.classList.remove('hidden');
        btnAddCart.classList.add('hidden');

        // 撈出所有副食與飲品來填充下拉選單
        const sides = allMenus.filter(m => m.category === 'Sides');
        const drinks = allMenus.filter(m => m.category === 'Drinks');
        document.getElementById('combo-side-select').innerHTML = sides.map(s => `<option value="${s.id}">${s.name} (+$${s.price})</option>`).join('');
        document.getElementById('combo-drink-select').innerHTML = drinks.map(d => `<option value="${d.id}">${d.name} (+$${d.price})</option>`).join('');

        // 綁定「單點」按鈕 (帶上數量)
        btnAlacarte.onclick = () => {
            const qty = parseInt(document.getElementById('modal-quantity').value) || 1;
            addToCart(itemId, qty);
            closeItemModal();
        };

        // 綁定「升級套餐」按鈕 (帶上數量，副食和飲料的數量也會跟著加倍)
        btnCombo.onclick = () => {
            const qty = parseInt(document.getElementById('modal-quantity').value) || 1;
            const selectedSideId = parseInt(document.getElementById('combo-side-select').value);
            const selectedDrinkId = parseInt(document.getElementById('combo-drink-select').value);

            addToCart(itemId, qty);
            addToCart(selectedSideId, qty);
            addToCart(selectedDrinkId, qty);

            alert(`🎉 成功加入購物車：\n${item.name} + 副食 + 飲品 (共 ${qty} 份)！`);
            closeItemModal();
        };

    } else {
        // 是副食或飲料：隱藏套餐選項，只顯示加入購物車按鈕
        comboOptions.classList.add('hidden');
        btnAlacarte.classList.add('hidden');
        btnCombo.classList.add('hidden');
        btnAddCart.classList.remove('hidden');

        // 綁定一般的「加入購物車」按鈕 (帶上數量)
        btnAddCart.onclick = () => {
            const qty = parseInt(document.getElementById('modal-quantity').value) || 1;
            addToCart(itemId, qty);
            closeItemModal();
        };
    }

    // 顯示彈窗
    document.getElementById('item-modal').classList.remove('hidden');
}

function closeItemModal() {
    document.getElementById('item-modal').classList.add('hidden');
}

// 模擬購物車 (加上支援數量參數)
function addToCart(menuId, quantity = 1) {
    const item = allMenus.find(m => m.id === menuId);
    console.log(`🛒 已加入購物車: ${item.name} x ${quantity}份 (單價: $${item.price}, 小計: $${item.price * quantity})`);
}

// 頁面載入後執行
document.addEventListener('DOMContentLoaded', fetchMenus);

// --- 測試優惠券專用腳本 ---
async function runCouponTest() {
    console.log("=== 開始測試優惠券結帳流程 ===");
    try {
        // 1. 先隨便註冊一個測試帳號 (若已經註冊過會報錯，我們用 catch 忽略它)
        try {
            await fetch(API_BASE_URL + '/auth/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    username: 'test_user',
                    password: 'password123',
                    email: 'test@example.com'
                })
            });
        } catch (e) {}

        // 2. 登入取得 JWT Token
        const loginRes = await fetch(API_BASE_URL + '/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username: 'test_user', password: 'password123' })
        });

        if (!loginRes.ok) throw new Error("登入失敗，請確認後端是否正常運作");
        const token = await loginRes.text();
        console.log("🔑 成功取得 Token");

        // 3. 準備下單資料：買 2 個培根堡(假設 ID 是 1，單價 200)，總價應為 400
        // 我們帶入 SUMMER88 優惠碼 (打 88 折)
        const orderPayload = {
            items: [
                { menuId: 1, quantity: 2 }
            ],
            couponCode: 'SUMMER88' // 👈 這裡帶入優惠碼
        };

        console.log("🛒 傳送訂單資料給後端...", orderPayload);

        // 4. 送出訂單給後端結帳
        const orderRes = await fetch(API_BASE_URL + '/orders', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + token // 記得帶上通行證
            },
            body: JSON.stringify(orderPayload)
        });

        if (!orderRes.ok) throw new Error(await orderRes.text());

        const orderResult = await orderRes.json();
        console.log("✅ 結帳成功！後端回傳明細：", orderResult);

        // 算出原價 (結帳金額 + 被扣掉的折扣金額)
        const originalPrice = orderResult.totalAmount + orderResult.discountAmount;

        alert(`🎉 測試成功！\n\n` +
              `🍔 原價總計: $${originalPrice}\n` +
              `🎟️ 使用優惠券: ${orderResult.coupon.code} (${orderResult.coupon.description})\n` +
              `💸 折扣金額: -$${orderResult.discountAmount}\n` +
              `💰 最後結帳金額: $${orderResult.totalAmount}`);

    } catch (error) {
        console.error("❌ 測試失敗:", error);
        alert("測試失敗，請打開瀏覽器開發者工具 (F12) 看 Console 了解詳情\n\n錯誤訊息: " + error.message);
    }
}