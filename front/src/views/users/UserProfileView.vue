<template>
  <div class="px-6 pb-40 pt-40 w-3/5 h-full">
    <div class="overflow-auto rounded-xl h-full">
      <table class="min-w-full h-full">
        <thead class="bg-gray-800 text-white color-F6C453">
          <tr class="rounded-xl">
            <th colspan="2" class="text-center py-3 px-4 uppercase font-semibold text-sm w-1/3">
              Twój profil
            </th>
          </tr>
        </thead>
        <tbody class="text-gray-700 h-full">
          <tr class="bg-orange-100 h-full">
            <td class="text-center px-10 py-20 h-1/2 w-1/2">
              <div class='border-4 py-4 px-2 rounded-xl border-black'>
                <div class='border-b-2 color-F6C453 border-red-500 bg-gray-800 rounded-xl text-black'>
                  <h1 class='text-center'>Edytuj Profil</h1>
                </div>
                <div class='mt-10'>
                  <span>Imię: </span>
                  {{ this.result.firstName }}
                  <PencilIcon class='inline-block align-middle float-right pb-1' @click='editProfile(this.result.firstName, "firstName")'/>
                </div>
                <div class='mt-10'>
                  <span>Nazwisko: </span>
                  {{ this.result.lastName }}
                  <PencilIcon class='inline-block align-middle float-right pb-1' @click='editProfile(this.result.lastName, "lastName")'/>
                </div>
                <div class='mt-10'>
                  <span>Email: </span>
                  {{ this.result.email }}
                  <PencilIcon class='inline-block align-middle float-right pb-1' @click='editProfile(this.result.email, "email")'/>
                </div>
                <div class='mt-6 text-center'>
                  <button class='color-F6C453 px-4 py-2 text-black rounded-xl' @click='editPassword()'> Edytuj Hasło </button>
                </div>
              </div>
            </td>
            <td class="text-center px-10 py-20 h-1/2 w-1/2">
              <div class='border-4 py-4 px-2 rounded-xl border-black flex flex-col h-full'>
                <div class='color-F6C453 border-b-2 border-red-500 bg-gray-800 rounded-xl text-black'>
                  <h1 class='text-center'>Twoje zapisy</h1>
                </div>
                <span v-for="save in resultSaves" :key="save">
                  <div class='mt-2 flex flex-row w-full'>
                    <div class='pl-8'>
                      <span @click='this.$router.push({ name:"creator", state: {redirectExport: save.netExport} })'>{{ save.saveName }}</span>
                    </div>
                    <div class='ml-auto'>
                      <ShareIcon v-if='!save.public' class='inline-block align-middle pb-1' @click='setPublicAlert(save, save.id)'/>
                      <LinkIcon class='inline-block align-middle pb-1' @click='showRefLinkModal(save.id)'/>
                      <PencilIcon class='inline-block align-middle pb-1' @click='editSave(save, save.saveName)'/>
                      <DeleteIcon class='inline-block align-middle pb-1' @click='deleteSaveNetAlert(save.id)'/>
                    </div>
                  </div>
                </span>
                <HomePaginationBar
                  ref='pagination'
                  class='mt-auto color-F6C453'
                  :size='this.size'
                  :type='this.type'
                />
                <RefLinkModal
                  :net='refSave'
                  v-show='isModalVisible'
                  @close='closeRefLinkModal()'
                />
                <span v-if='this.resultSaves.length == 0' class='my-auto'>Nie zapisałeś jeszcze żadnych sieci!</span>
              </div>
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
import UserServices, { IUser } from '@/services/UserService';
import Swal from 'sweetalert2';
import { AxiosError } from 'axios';

import PencilIcon from 'vue-material-design-icons/Pencil.vue';
import ShareIcon from 'vue-material-design-icons/Share.vue';
import DeleteIcon from 'vue-material-design-icons/Delete.vue';
import LinkIcon from 'vue-material-design-icons/Link.vue';

import HomePaginationBar from '../../components/HomePaginationBar.vue';
import RefLinkModal from '../../components/RefLinkModal.vue';

export default defineComponent({
  components: {
    PencilIcon,
    ShareIcon,
    DeleteIcon,
    LinkIcon,
    HomePaginationBar,
    RefLinkModal
  },
  data() {
    return {
      result: LoginServices.getBlankLoginTemplate(),
      resultSaves: [SaveNetServices.getBlankSaveNetTemplate()],
      resultEdit: UserServices.getBlankUserTemplate(),
      resultEditSave: SaveNetServices.getBlankSaveNetTemplate(),
      isModalVisible: false,
      refSave: 0,
      size: 0,
      selected: 0,
      pageSize: 6,
      type: 'profile'
    };
  },

  watch: {
    result() {
      if (this.result.role === '') {
        this.$router.push('/');
      }
    }
  },

  mounted() {
    if (localStorage.getItem('token')) {
      this.getUser().then((data) => (this.result = data));
      this.getSaves().then((data) => (this.size = data.length));

      this.getSavesPaginated(this.selected, this.pageSize).then((data) => (this.resultSaves = data));

      this.$watch(
        '$refs.pagination.selected',
        (newVal: any) => {
          this.selected = newVal;
          this.getSavesPaginated(this.selected, this.pageSize).then((data) => (this.resultSaves = data));
        }
      );
    } else {
      this.$router.push('/');
    }
  },

  methods: {
    async getUser(): Promise<ILogin> {
      return await LoginServices.fetch();
    },

    async getSaves(): Promise<Array<ISaveNet>> {
      return (await SaveNetServices.fetchSavedNets((await this.getUser()).email));
    },

    async deleteSaveNet(saveNetId: number): Promise<any> {
      try {
        await SaveNetServices.delete(saveNetId);
      } catch (e: any) {
        const error = e.response?.status as AxiosError;
        return error;
      }
      return 200;
    },

    async getSavesPaginated(page: number, pageSize: number): Promise<Array<ISaveNet>> {
      return (await SaveNetServices.fetchSavedNetsPaginated((await this.getUser()).email, page, pageSize));
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

    async findByUserAndSaveName(saveName: string): Promise<number> {
      return await SaveNetServices.findBySaveNameAndId(saveName);
    },

    async editSaveName(id: number): Promise<ISaveNet> {
      return await SaveNetServices.update(this.resultEditSave, id);
    },

    async setPublic(save: ISaveNet, id: number): Promise<ISaveNet> {
      return await SaveNetServices.setPublic(save, id);
    },

    logout(): void {
      localStorage.removeItem('token');
      if (this.$route.name === 'home') {
        window.location.reload();
      } else {
        this.$router.push('/');
      }
    },

    editSave(save: ISaveNet, saveName: string) {
      this.resultEditSave = SaveNetServices.getBlankSaveNetTemplate();
      Swal.fire({
        title: 'Edytujesz nazwę ' + saveName,
        html: '<input type="text" id="edit" class="swal2-input">',
        cancelButtonText: 'Anuluj',
        showCancelButton: true,
        preConfirm: () => {
          const editValue = (document.getElementById('edit') as HTMLInputElement).value;
          if (!editValue) {
            Swal.showValidationMessage('Pole jest puste!');
          }
          if (editValue.length < 3 && editValue.length > 0) {
            Swal.showValidationMessage('Podana nazwa jest za krótka!');
          }

          return { editValue };
        }
      }).then((result) => {
        if (result.value) {
          this.findByUserAndSaveName(result.value.editValue).then((found) => {
            if (found !== 0) {
              Swal.fire({
                icon: 'error',
                title: 'Błąd!',
                text: 'Istnieje już taka nazwa!'
              });
            } else {
              this.resultEditSave.saveName = result.value?.editValue as string;
              this.resultEditSave.userId = this.result.id;
              this.editSaveName(save.id);
              Swal.fire(
                'Gotowe!',
                'Twoja nazwa została zmieniona.',
                'success'
              );
              this.getSavesPaginated(this.selected, this.pageSize).then((data) => (this.resultSaves = data));
            }
          });
        }
      });
    },

    editProfile(name: string, type: string) {
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

    deleteSaveNetAlert(saveNetId: number) {
      Swal.fire({
        title: 'Jesteś pewien?',
        text: 'Nie będziesz w stanie tego cofnąć!',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Tak, usuń!',
        cancelButtonText: 'Anuluj'
      }).then((result) => {
        if (result.isConfirmed) {
          this.deleteSaveNet(saveNetId).then((response) => {
            if (response !== 200) {
              Swal.fire(
                'Błąd!',
                'Nie można usunąć sieci.',
                'error'
              );
              this.getSavesPaginated(this.selected, this.pageSize).then((data) => (this.resultSaves = data));
            } else {
              Swal.fire(
                'Gotowe!',
                'Sieć została usunięta.',
                'success'
              );
              this.getSavesPaginated(this.selected, this.pageSize).then((data) => (this.resultSaves = data));
              this.getSaves().then((data) => (this.size = data.length));
            }
          });
        }
      });
    },

    setPublicAlert(save: ISaveNet, id: number) {
      Swal.fire({
        icon: 'info',
        title: 'Czy napewno chcesz ustawić swój model jako publiczny?',
        text: 'Bo zmianie, będzie on widoczny dla innych użytkowników na stronie głównej!',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Tak',
        cancelButtonText: 'Anuluj'
      }).then((result) => {
        if (result.isConfirmed) {
          this.setPublic(save, id);
          Swal.fire(
            'Gotowe!',
            'Twój model został udostępniony.',
            'success'
          );
          this.getSavesPaginated(this.selected, this.pageSize).then((data) => (this.resultSaves = data));
        }
      });
    },

    editPassword() {
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
    },

    showRefLinkModal(save: number) {
      this.refSave = save;
      this.isModalVisible = true;
    },

    closeRefLinkModal() {
      this.isModalVisible = false;
    }
  }
});
</script>
