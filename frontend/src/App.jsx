import { defineComponent, ref, computed, onMounted } from 'vue';

export default defineComponent({
  name: 'App',
  setup() {
    // === 狀態定義 ===
    const API_BASE_URL = "http://localhost:8080/api";
    const allMenus = ref([]);
    const currentCategory = ref('All');
    const isLoading = ref(true);
    const hasError = ref(false);

    // 彈窗狀態
    const isModalOpen = ref(false);
    const selectedItem = ref(null);
    const quantity = ref(1);
    const selectedSideId = ref('');
    const selectedDrinkId = ref('');

    // === 計算屬性 (取代原本的手動 filter 過濾) ===
    const filteredMenus = computed(() => {
      if (currentCategory.value === 'All') return allMenus.value;
      return allMenus.value.filter(item => item.category === currentCategory.value);
    });

    const sides = computed(() => allMenus.value.filter(m => m.category === 'Sides'));
    const drinks = computed(() => allMenus.value.filter(m => m.category === 'Drinks'));
    const isBurger = computed(() => selectedItem.value?.category === 'Burgers');

    // === 方法定義 ===
    const fetchMenus = async () => {
      try {
        const response = await fetch(`${API_BASE_URL}/menus`);
        if (!response.ok) throw new Error("網路請求失敗");
        allMenus.value = await response.json();
      } catch (error) {
        console.error("無法取得菜單:", error);
        hasError.value = true;
      } finally {
        isLoading.value = false;
      }
    };

    const filterCategory = (category) => {
      currentCategory.value = category;
    };

    const openModal = (item) => {
      selectedItem.value = item;
      quantity.value = 1;
      // 預設選擇第一項副食與飲料
      if (sides.value.length > 0) selectedSideId.value = sides.value[0].id;
      if (drinks.value.length > 0) selectedDrinkId.value = drinks.value[0].id;
      isModalOpen.value = true;
    };

    const closeModal = () => {
      isModalOpen.value = false;
      selectedItem.value = null;
    };

    const changeQuantity = (delta) => {
      let val = quantity.value + delta;
      if (val < 1) val = 1;
      if (val > 99) val = 99;
      quantity.value = val;
    };

    const addToCart = (menuId, qty) => {
      const item = allMenus.value.find(m => m.id === menuId);
      if(item) {
          console.log(`🛒 已加入購物車: ${item.name} x ${qty}份 (單價: $${item.price}, 小計: $${item.price * qty})`);
      }
    };

    const handleAlacarte = () => {
      addToCart(selectedItem.value.id, quantity.value);
      closeModal();
    };

    const handleCombo = () => {
      addToCart(selectedItem.value.id, quantity.value);
      addToCart(selectedSideId.value, quantity.value);
      addToCart(selectedDrinkId.value, quantity.value);
      alert(`🎉 成功加入購物車：\n${selectedItem.value.name} + 副食 + 飲品 (共 ${quantity.value} 份)！`);
      closeModal();
    };

    // 原本的測試腳本
    const runCouponTest = async () => {
        // (此處保留你原本的測試邏輯，為了版面簡潔略過實作細節，你可以直接把原本的程式碼貼進來)
        console.log("=== 開始測試優惠券結帳流程 ===");
        alert("測試腳本已觸發！請查看 Console。");
    };

    // === 生命週期 ===
    onMounted(() => {
      fetchMenus();
    });

    // === 渲染函數 (JSX) ===
    return () => (
      <div class="min-h-screen">
        {/* Navbar */}
        <nav class="p-6 border-b border-stone-700 flex justify-between items-center">
          <h1 class="text-2xl font-black text-yellow-500 uppercase tracking-widest">Rocket Burger</h1>
          <button onClick={runCouponTest} class="text-sm text-yellow-500 underline">執行測試腳本</button>
        </nav>

        {/* 主要內容 */}
        <main class="max-w-7xl mx-auto p-8">
          <h2 class="text-4xl font-extrabold mb-8 border-l-8 border-red-600 pl-4">MENU 餐點介紹</h2>

          {/* 分類按鈕 */}
          <div class="flex flex-wrap gap-4 mb-10">
            {['All', 'Burgers', 'Sides', 'Drinks'].map(cat => (
              <button
                key={cat}
                onClick={() => filterCategory(cat)}
                class={`cat-btn px-6 py-2 rounded-full font-bold transition-colors ${
                  currentCategory.value === cat
                    ? 'bg-yellow-500 text-black'
                    : 'bg-stone-800 hover:bg-stone-700 text-white'
                }`}
              >
                {cat === 'All' ? '全部餐點' : cat === 'Burgers' ? '經典主食' : cat === 'Sides' ? '美味副食' : '清涼飲品'}
              </button>
            ))}
          </div>

          {/* 餐點列表 */}
          <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {isLoading.value ? (
               <p class="col-span-full text-center py-20 text-stone-500 italic">菜單載入中...</p>
            ) : hasError.value ? (
               <p class="col-span-full text-center py-20 text-red-500">無法載入菜單，請檢查後端是否啟動。</p>
            ) : filteredMenus.value.length === 0 ? (
               <p class="col-span-full text-center py-10 text-stone-500">此分類目前沒有餐點喔！</p>
            ) : (
              filteredMenus.value.map(item => (
                <div key={item.id} class="bg-stone-800 p-4 rounded-xl border border-stone-700 hover:border-yellow-500 transition-all group flex flex-col justify-between">
                  <div>
                    <div class="h-40 bg-stone-700 rounded-lg mb-4 flex items-center justify-center text-stone-500 overflow-hidden">
                      {item.imageUrl
                        ? <img src={item.imageUrl} class="w-full h-full object-cover" />
                        : <span class="italic">Rocket Burger</span>
                      }
                    </div>
                    <h3 class="text-xl font-bold">{item.name}</h3>
                    <span class="text-xs bg-stone-900 text-stone-400 px-2 py-1 rounded inline-block mt-1">{item.category}</span>
                    <p class="text-stone-400 text-sm mt-2 h-10 overflow-hidden">{item.description || '美味推薦'}</p>
                    <p class="text-yellow-500 text-2xl font-black mt-2">${item.price}</p>
                  </div>
                  <button onClick={() => openModal(item)} class="w-full mt-4 bg-red-600 hover:bg-red-500 py-2 rounded-lg font-bold transition-colors">
                    選擇餐點
                  </button>
                </div>
              ))
            )}
          </div>
        </main>

        {/* 彈窗 (Modal) */}
        {isModalOpen.value && selectedItem.value && (
          <div class="fixed inset-0 bg-black/80 flex justify-center items-center z-50 p-4">
            <div class="bg-stone-800 p-6 rounded-2xl max-w-md w-full border border-stone-600 shadow-2xl max-h-[90vh] overflow-y-auto">

              <div class="h-48 bg-stone-700 rounded-lg mb-4 flex items-center justify-center overflow-hidden">
                {selectedItem.value.imageUrl
                  ? <img src={selectedItem.value.imageUrl} class="w-full h-full object-cover" />
                  : <span class="italic text-stone-500">Rocket Burger</span>
                }
              </div>

              <h3 class="text-3xl font-black text-yellow-500 mb-1 uppercase">{selectedItem.value.name}</h3>
              <p class="text-stone-300 mb-3 text-sm">{selectedItem.value.description || '無詳細描述'}</p>
              <p class="text-xl font-bold text-white mb-4">${selectedItem.value.price}</p>

              {/* 數量控制 */}
              <div class="flex items-center gap-4 mb-6 bg-stone-900 p-3 rounded-lg border border-stone-700 w-fit">
                <span class="text-stone-400 text-sm">數量</span>
                <button onClick={() => changeQuantity(-1)} class="w-8 h-8 bg-stone-700 hover:bg-stone-600 rounded-full text-white font-bold flex items-center justify-center transition-colors">-</button>
                <input
                  type="number"
                  v-model={quantity.value}
                  min="1" max="99"
                  class="w-12 text-center bg-transparent text-white font-bold outline-none no-spinners"
                />
                <button onClick={() => changeQuantity(1)} class="w-8 h-8 bg-stone-700 hover:bg-stone-600 rounded-full text-white font-bold flex items-center justify-center transition-colors">+</button>
              </div>

              {/* 漢堡專屬：套餐選項 */}
              {isBurger.value && (
                <div class="bg-stone-900 p-4 rounded-lg mb-6 border border-stone-700">
                  <h4 class="font-bold text-white mb-4">✨ 升級套餐 (任選副食 + 飲品)</h4>
                  <div class="mb-4">
                    <label class="block text-sm text-stone-400 mb-2">🍟 選擇副食</label>
                    <select v-model={selectedSideId.value} class="w-full bg-stone-800 text-white p-3 rounded-lg outline-none focus:ring-2 focus:ring-yellow-500 border border-stone-600">
                      {sides.value.map(s => <option key={s.id} value={s.id}>{s.name} (+${s.price})</option>)}
                    </select>
                  </div>
                  <div>
                    <label class="block text-sm text-stone-400 mb-2">🥤 選擇飲品</label>
                    <select v-model={selectedDrinkId.value} class="w-full bg-stone-800 text-white p-3 rounded-lg outline-none focus:ring-2 focus:ring-yellow-500 border border-stone-600">
                      {drinks.value.map(d => <option key={d.id} value={d.id}>{d.name} (+${d.price})</option>)}
                    </select>
                  </div>
                </div>
              )}

              {/* 按鈕群 */}
              <div class="flex gap-3">
                <button onClick={closeModal} class="w-1/4 bg-stone-700 hover:bg-stone-600 text-white py-3 rounded-lg font-bold transition-colors text-sm">取消</button>

                {!isBurger.value ? (
                  <button onClick={handleAlacarte} class="flex-1 bg-red-600 hover:bg-red-500 text-white py-3 rounded-lg font-bold transition-colors">加入購物車</button>
                ) : (
                  <>
                    <button onClick={handleAlacarte} class="w-1/3 bg-stone-600 hover:bg-stone-500 text-white py-3 rounded-lg font-bold transition-colors">單點</button>
                    <button onClick={handleCombo} class="flex-1 bg-red-600 hover:bg-red-500 text-white py-3 rounded-lg font-bold transition-colors">升級套餐</button>
                  </>
                )}
              </div>
            </div>
          </div>
        )}
      </div>
    );
  }
});