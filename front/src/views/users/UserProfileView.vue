<template>
  <div class="px-6 pb-40 pt-40 w-3/5 h-full">
    <div class="overflow-auto rounded-xl h-full">
      <table class="min-w-full h-full">
        <thead class="bg-gray-800 text-white">
          <tr class="rounded-xl">
            <th colspan="2" class="text-center py-3 px-4 uppercase font-semibold text-sm w-1/3">
              Twój profil
            </th>
          </tr>
        </thead>
        <tbody class="text-gray-700 h-full">
          <tr class="bg-white h-full">
            <td class="text-center px-10 py-20 h-1/2 w-1/2">
              <div class='border-4 py-4 px-2 rounded-xl border-black'>
                <div class='border-b-2 border-red-500 bg-gray-800 rounded-xl text-red-50'>
                  <h1 class='text-center'>Edytuj Profil</h1>
                </div>
                <div class='mt-10'>
                  <span>Imię: </span>
                  {{ this.result.firstName }}
                  <PencilIcon class='inline-block align-middle float-right pb-1' @click='EditProfile(this.result.firstName, "firstName")'/>
                </div>
                <div class='mt-10'>
                  <span>Nazwisko: </span>
                  {{ this.result.lastName }}
                  <PencilIcon class='inline-block align-middle float-right pb-1' @click='EditProfile(this.result.lastName, "lastName")'/>
                </div>
                <div class='mt-10'>
                  <span>Email: </span>
                  {{ this.result.email }}
                  <PencilIcon class='inline-block align-middle float-right pb-1' @click='EditProfile(this.result.email, "email")'/>
                </div>
                <div class='mt-6 text-center'>
                  <button class='bg-gray-600 px-4 py-2 text-white rounded-xl' @click='EditPassword()'> Edytuj Hasło </button>
                </div>
              </div>
            </td>
            <td class="text-center px-4 pt-8 pb-4 h-full w-1/2 align-top">
              <span>Twoje zapisy</span> <br>
              <template v-for="save in resultSaves" :key="save">
                <span>{{ save.saveName }}</span><br>
              </template>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import SaveNetServices, { ISaveNet } from '@/services/SaveNetService';
import LoginServices, { ILogin } from '@/services/LoginService';
import PencilIcon from 'vue-material-design-icons/Pencil.vue';
import UserServices, { IUser } from '@/services/UserService';
import Swal from 'sweetalert2';
import { AxiosError } from 'axios';

export default defineComponent({
  components: {
    PencilIcon
  },
  data() {
    return {
      result: LoginServices.getBlankLoginTemplate(),
      resultSaves: [SaveNetServices.getBlankSaveNetTemplate()],
      resultEdit: UserServices.getBlankUserTemplate()
    };
  },

  mounted() {
    this.getUser().then((data) => (this.result = data));
    this.getSaves().then((data) => (this.resultSaves = data));
  },

  methods: {
    async getUser(): Promise<ILogin> {
      return await LoginServices.fetch();
    },

    async getSaves(): Promise<Array<ISaveNet>> {
      return (await SaveNetServices.fetchSavedNets((await this.getUser()).email));
    },

    async editUserProfile(): Promise<any> {
      try {
        await UserServices.editProfile(this.result.id, this.resultEdit);
      } catch (e: any) {
        const error = e.response?.status as AxiosError;
        return error;
      }
      return 200;
    },

    logout(): void {
      localStorage.removeItem('token');
      if (this.$route.name === 'home') {
        window.location.reload();
      } else {
        this.$router.push('/');
      }
    },

    EditProfile(name: string, type: string) {
      this.resultEdit = UserServices.getBlankUserTemplate();
      Swal.fire({
        title: 'Edytujesz: ' + name,
        html: '<input type="text" id="edit" class="swal2-input">',
        cancelButtonText: 'Anuluj',
        showCancelButton: true,
        preConfirm: () => {
          const editValue = (document.getElementById('edit') as HTMLInputElement).value;
          if (!editValue) {
            Swal.showValidationMessage('Pole jest puste!');
          }
          if (editValue.length < 3) {
            Swal.showValidationMessage('Podana nazwa jest za krótka');
          }
          if (type === 'email' && !this.regexEmail(editValue)) {
            Swal.showValidationMessage('Niepoprawny adres email!');
          }

          return { editValue };
        }
      }).then((result) => {
        if (result.value) {
          if (type === 'firstName') {
            this.resultEdit.firstName = result.value.editValue;
          } else if (type === 'lastName') {
            this.resultEdit.lastName = result.value.editValue;
          } else {
            this.resultEdit.email = result.value.editValue;
          }
          this.editUserProfile().then((error) => {
            if (error !== 200) {
              Swal.fire({
                icon: 'error',
                title: 'Błąd!',
                text: 'Wystąpił błąd!'
              });
            } else {
              Swal.fire({
                title: 'Gotowe!',
                text: 'Za chwilę zostaniesz wylogowany!',
                icon: 'success',
                timer: 3000
              }).then(() =>
                this.logout());
            }
          });
        }
      });
    },

    EditPassword() {
      this.resultEdit = UserServices.getBlankUserTemplate();
      Swal.fire({
        title: 'Edytujesz hasło',
        html: `<input type="password" id="password" class="swal2-input" placeholder="Hasło">
               <input type="password" id="password_check" class="swal2-input" placeholder="Powtórz hasło">`,
        cancelButtonText: 'Anuluj',
        focusConfirm: false,
        showCancelButton: true,
        preConfirm: () => {
          const password = (document.getElementById('password') as HTMLInputElement).value;
          const passwordCheck = (document.getElementById('password_check') as HTMLInputElement).value;

          if (!password || !passwordCheck) {
            Swal.showValidationMessage('Podaj hasło');
          }

          if (passwordCheck.length < 3 || password.length < 3) {
            Swal.showValidationMessage('Podane hasło jest za krótkie!');
          }

          if (password !== passwordCheck) {
            Swal.showValidationMessage('Hasła się nie zgadzają!');
          }

          if (!this.regexPassword(password)) {
            Swal.showValidationMessage('Podane hasło jest złe. Hasło powinno posiadać przynajmniej 8 znaków, dużą literę oraz cyfrę!');
          }

          return { password };
        }
      }).then((result) => {
        if (result.value) {
          this.resultEdit.password = result.value.password;
          this.editUserProfile().then((error) => {
            if (error !== 200) {
              Swal.fire({
                icon: 'error',
                title: 'Błąd!',
                text: 'Wystąpił błąd!'
              });
            } else {
              Swal.fire({
                title: 'Gotowe!',
                text: 'Za chwilę zostaniesz wylogowany!',
                icon: 'success',
                timer: 3000
              }).then(() =>
                this.logout());
            }
          });
        }
      });
    },

    regexEmail(email: string) {
      const regex = /^[A-Za-z-\\.]+[A-Za-z0-9-\\.]+@([\w-]+\.)+[\w-]{2,4}$/;
      const matches = regex.exec(email);
      return matches;
    },

    regexPassword(password: string) {
      const regex = /^(?=.*[A-Z])(?=.*\d)[A-Za-z\d]{8,16}$/;
      const matches = regex.exec(password);
      return matches;
    }
  }
});
</script>
