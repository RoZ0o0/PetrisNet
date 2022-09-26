<template>
  <div class="container" id="cont">
    <div class="nav">
      <div class="logo_content">
        <div class="logo">
          <img class="material-icons ml-3"/>
        </div>
      </div>
      <ul class="nav_list border">
        <li>
          <router-link to="/users">
            <span>User</span>
          </router-link>
        </li>
        <li>
          <router-link to ="/">
            <span>Home</span>
          </router-link>
        </li>
      </ul>
    </div>
  </div>
</template>
