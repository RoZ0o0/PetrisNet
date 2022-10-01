<template>
  <div class="w-screen max-h-full rounded-b-xl">
    <div class="rounded-b-xl">
      <ul class="bg-red-500 rounded-b-xl">
        <router-link to ="/">
          <li>
            <span>Home</span>
          </li>
        </router-link>
        <router-link to="/users">
          <li>
            <span>User</span>
          </li>
        </router-link>
        <router-link to="/login">
          <li class="float-right">
            <span>Login</span>
          </li>
        </router-link>
      </ul>
    </div>
  </div>
</template>
