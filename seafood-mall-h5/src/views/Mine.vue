<!--
  ICodeStar 智码星科技
  https://www.icodestar.net
  
  Copyright (c) 2025 ICodeStar. All rights reserved.
  Licensed under the MIT License.
-->

<template>
  <van-nav-bar title="我的" fixed placeholder />
  <div class="mine-container">
    <template v-if="!userStore.isLoggedIn">
      <van-cell-group inset class="login-register-group">
        <van-cell title="登录" is-link @click="toLogin" />
        <van-cell title="注册" is-link @click="toRegister" />
      </van-cell-group>
    </template>
    <template v-else>
      <div class="user-info-panel">
        <van-image
          round
          width="60"
          height="60"
          src="https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg"
          class="user-avatar"
        />
        <div class="user-detail">
          <p class="username">欢迎您，{{ userStore.username }}！</p>
          <p class="realname" v-if="userStore.realName">真实姓名：{{ userStore.realName }}</p>
          <p class="user-level">用户等级：{{ userLevelText }}</p>
        </div>
      </div>

      <van-cell-group inset title="我的订单" class="module-group">
        <van-cell title="我的订单" is-link icon="orders-o" @click="goToMyOrders" />
      </van-cell-group>

      <van-cell-group inset title="地址管理" class="module-group">
        <van-cell title="收货地址" is-link icon="location-o" @click="goToAddresses" />
      </van-cell-group>

      <van-cell-group inset title="支付方式" class="module-group">
        <van-cell title="支持的支付方式">
          <template #value>
            <p v-if="userStore.userLevel === 0">在线支付</p>
            <p v-else-if="userStore.userLevel === 1">在线支付，储值卡支付</p>
            <p v-else-if="userStore.userLevel === 2">在线支付，储值卡支付，按月结算支付</p>
          </template>
        </van-cell>
      </van-cell-group>

      <van-cell-group inset title="我的资产" class="module-group">
        <van-cell title="储值卡" is-link value="查看详情" @click="Toast('储值卡功能待开发...')" />
        <van-cell title="" value="储值卡用户可享受9折优惠" v-if="userStore.userLevel >= 1">
          <template #icon>
            <van-icon name="discount" size="18" />
          </template>
        </van-cell>
        <van-cell title="购物券" is-link value="查看详情" @click="Toast('购物券功能待开发...')" />
        <van-cell title="礼品券" is-link value="查看详情" @click="Toast('礼品券功能待开发...')" />
      </van-cell-group>

      <div style="margin: 16px;">
        <van-button round block type="danger" @click="logout">退出登录</van-button>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '../stores/user';
import { Toast } from 'vant';

const router = useRouter();
const userStore = useUserStore();

const userLevelText = computed(() => {
  switch (userStore.userLevel) {
    case 0:
      return '普通用户';
    case 1:
      return '银牌用户';
    case 2:
      return '金牌用户';
    default:
      return '未知等级';
  }
});

const toLogin = () => {
  router.push('/login');
};

const toRegister = () => {
  router.push('/register');
};

const logout = () => {
  userStore.logout();
  Toast.success('已退出登录');
  router.push('/login');
};

const goToMyOrders = () => {
  router.push('/my-orders');
};

const goToAddresses = () => {
  router.push('/addresses');
};
</script>

<style scoped>
.mine-container {
  padding-bottom: 50px; /* 为底部标签栏留出空间 */
}

.login-register-group {
  margin-top: 50px;
}

.user-info-panel {
  display: flex;
  align-items: center;
  padding: 20px;
  background: linear-gradient(to right, #4facfe 0%, #00f2fe 100%); /* 渐变背景 */
  color: white;
  margin-bottom: 20px;
  border-bottom-left-radius: 10px;
  border-bottom-right-radius: 10px;
}

.user-avatar {
  margin-right: 15px;
  border: 2px solid white;
}

.user-detail p {
  margin: 2px 0;
}

.username {
  font-size: 18px;
  font-weight: bold;
}

.realname, .user-level {
  font-size: 14px;
  opacity: 0.9;
}

.module-group {
  margin-bottom: 10px;
}

/* 覆盖 Vant 的 inset 样式以减少左右边距，使其更贴近边缘 */
.van-cell-group--inset {
  margin: 10px 10px;
}

.van-cell-group__title {
  padding-left: 10px;
}
</style> 