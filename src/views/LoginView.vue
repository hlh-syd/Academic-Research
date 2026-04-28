<template>
  <div class="login-view">
    <div class="hero-orb hero-orb--blue login-orb login-orb--one"></div>
    <div class="hero-orb hero-orb--teal login-orb login-orb--two"></div>

    <section class="login-view__hero">
      <span class="eyebrow">Login</span>
      <h1>把文献调研、知识库与 AI 助手整合到一个工作台。</h1>
      <p>
        登录后可直接进入研究工作区。页面样式与主前端保持一致，认证流程走后端
        <code>/api/auth/login</code>。
      </p>

      <div class="login-view__hero-grid">
        <article class="login-view__hero-card">
          <strong>学术调研</strong>
          <span>论文搜索、思维导图、快速总结与历史追踪。</span>
        </article>
        <article class="login-view__hero-card">
          <strong>知识库</strong>
          <span>文件夹管理、上传归档与知识问答沉淀。</span>
        </article>
        <article class="login-view__hero-card">
          <strong>研究计划</strong>
          <span>任务拆解、专注计时与日历打卡协作。</span>
        </article>
      </div>
    </section>

    <section class="glass-panel login-view__panel">
      <div class="login-view__panel-top">
        <div class="sidebar__brand-mark"></div>
        <div>
          <div class="login-view__brand">Academic Research Workbench</div>
          <p>统一风格登录页 + 后端 JWT 认证。</p>
        </div>
      </div>

      <form class="login-view__form" @submit.prevent="goToWorkspace">
        <div>
          <label>邮箱</label>
          <el-input
            v-model="email"
            class="app-input"
            size="large"
            placeholder="researcher@lab.ai"
          />
        </div>
        <div>
          <label>密码</label>
          <el-input
            v-model="password"
            class="app-input"
            size="large"
            show-password
            type="password"
            placeholder="请输入密码"
          />
        </div>
        <p v-if="errorMessage" class="login-view__error">{{ errorMessage }}</p>
        <div class="login-view__checks">
          <span class="glass-chip">支持 JWT 登录</span>
          <span class="glass-chip">登录成功后自动进入系统</span>
        </div>
        <GlassButton
          class="login-view__submit"
          native-type="submit"
          variant="primary"
          :disabled="pending"
        >
          <template #icon>
            <Right style="width: 16px; height: 16px" />
          </template>
          {{ pending ? '登录中...' : '进入系统' }}
        </GlassButton>
      </form>

      <div class="login-view__footer">
        <div>
          <span class="muted">前端栈</span>
          <strong>Vue 3 + Vite + Element Plus + Pinia + Axios</strong>
        </div>
        <span class="status-badge">已接入后端认证</span>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { Right } from '@element-plus/icons-vue';
import type { AxiosError } from 'axios';

import GlassButton from '@/components/ui/GlassButton.vue';
import { http } from '@/api/http';

interface ApiResponse<T> {
  success: boolean;
  data: T;
  message: string;
}

interface LoginPayload {
  token: string;
  email: string;
  displayName: string;
  role: string;
}

const router = useRouter();
const email = ref('researcher@lab.ai');
const password = ref('12345678');
const pending = ref(false);
const errorMessage = ref('');

const goToWorkspace = async () => {
  if (pending.value) {
    return;
  }

  pending.value = true;
  errorMessage.value = '';

  try {
    const { data } = await http.post<ApiResponse<LoginPayload>>('/auth/login', {
      email: email.value,
      password: password.value,
    });

    const token = data?.data?.token;
    if (!token) {
      throw new Error('登录成功但未收到 token');
    }

    localStorage.setItem('token', token);
    localStorage.setItem('user', JSON.stringify(data.data));
    await router.push('/app/research');
  } catch (rawError) {
    const error = rawError as AxiosError<ApiResponse<unknown>>;
    errorMessage.value =
      error.response?.data?.message || error.message || '登录失败，请检查账号密码后重试。';
  } finally {
    pending.value = false;
  }
};
</script>

<style scoped>
.login-view {
  position: relative;
  display: grid;
  grid-template-columns: minmax(0, 1.1fr) 440px;
  gap: 36px;
  min-height: 100vh;
  padding: 28px;
  overflow: hidden;
}

.login-view__hero,
.login-view__panel {
  position: relative;
  z-index: 1;
}

.login-view__hero {
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-height: calc(100vh - 56px);
  padding: 48px 46px;
  border-radius: 42px;
  background:
    radial-gradient(circle at 15% 10%, rgba(110, 138, 255, 0.28), transparent 30%),
    radial-gradient(circle at 80% 18%, rgba(25, 183, 160, 0.2), transparent 24%),
    linear-gradient(135deg, rgba(255, 255, 255, 0.96), rgba(242, 247, 255, 0.88));
  border: 1px solid rgba(255, 255, 255, 0.7);
  box-shadow: var(--shadow-glass);
}

.login-view__hero h1 {
  margin: 20px 0 0;
  max-width: 780px;
  font-family: 'Manrope', 'Inter', sans-serif;
  font-size: 64px;
  line-height: 1.02;
  letter-spacing: -0.06em;
}

.login-view__hero p {
  max-width: 620px;
  margin: 22px 0 0;
  color: var(--text-soft);
  font-size: 18px;
  line-height: 1.8;
}

.login-view__hero code {
  font-size: 0.92em;
  color: #1f4de0;
}

.login-view__hero-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 18px;
  margin-top: 40px;
}

.login-view__hero-card {
  padding: 22px;
  border-radius: 30px;
  background: rgba(255, 255, 255, 0.72);
  border: 1px solid rgba(200, 211, 231, 0.55);
}

.login-view__hero-card strong {
  display: block;
  font-size: 20px;
  font-family: 'Manrope', 'Inter', sans-serif;
}

.login-view__hero-card span {
  display: block;
  margin-top: 12px;
  color: var(--text-soft);
  line-height: 1.7;
}

.login-view__panel {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 28px;
  padding: 34px;
  border-radius: 40px;
}

.login-view__panel-top {
  display: flex;
  gap: 18px;
}

.login-view__brand {
  font-family: 'Manrope', 'Inter', sans-serif;
  font-size: 28px;
  font-weight: 800;
  letter-spacing: -0.06em;
}

.login-view__panel-top p {
  margin: 8px 0 0;
  color: var(--text-soft);
}

.login-view__form {
  display: grid;
  gap: 18px;
}

.login-view__form label {
  display: block;
  margin-bottom: 10px;
  color: var(--text-faint);
  font-size: 13px;
  font-weight: 700;
  letter-spacing: 0.06em;
  text-transform: uppercase;
}

.login-view__checks {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.login-view__error {
  margin: -2px 0 0;
  color: #b42318;
  font-size: 13px;
}

.login-view__submit {
  width: 100%;
}

.login-view__submit:disabled {
  opacity: 0.75;
  cursor: not-allowed;
}

.login-view__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding-top: 8px;
}

.login-view__footer strong {
  display: block;
  margin-top: 6px;
}

.login-orb {
  width: 420px;
  height: 420px;
}

.login-orb--one {
  left: -120px;
  top: -80px;
}

.login-orb--two {
  right: 120px;
  bottom: -140px;
}

.sidebar__brand-mark {
  width: 56px;
  height: 56px;
  border-radius: 20px;
  background:
    radial-gradient(circle at 30% 30%, rgba(255, 255, 255, 0.85), rgba(255, 255, 255, 0) 52%),
    linear-gradient(160deg, #427bff, #21c4a4);
  box-shadow:
    inset 0 1px 1px rgba(255, 255, 255, 0.8),
    0 18px 36px rgba(61, 102, 250, 0.24);
}

@media (max-width: 1280px) {
  .login-view {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 900px) {
  .login-view {
    padding: 14px;
  }

  .login-view__hero,
  .login-view__panel {
    padding: 24px;
    border-radius: 24px;
  }

  .login-view__hero h1 {
    font-size: 40px;
  }

  .login-view__hero-grid {
    grid-template-columns: 1fr;
  }

  .login-view__footer {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
