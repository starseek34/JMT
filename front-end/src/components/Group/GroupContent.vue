<template>
  <v-row>
    <!-- 좌측 그룹 정보 부분 -->
    <v-col cols="5" style="vertical-align:middle; padding-top: 20px; margin-left : 40px;">
      <v-row justify="center">
        <v-col id="onLineStatus">
          <v-row>
            <div id="conferenceStatusBox" style="width:100%;">
            <p
              v-if="(nowMeeting || this.conferenceOn)"
              class="conferenceStatus"
              style="color: red; border: 2px solid red;"
            >회의 진행중</p>
            <p
              v-else
              class="conferenceStatus"
              style="color: green; border: 2px solid green;"
            >회의 진행중이 아닙니다</p>
            </div>
          </v-row>
        </v-col>
      </v-row>
      <v-row justify="center">
        <v-col id="conferenceBox">
          <div>
            <v-row style="margin-bottom : -10px; padding: 0px auto ">
              <v-col>
                <div id="GroupContentgroupName" style="font-size:22px; float:left; color:Black; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">{{ groupInfo.groupName }}</div>
                <v-btn style="float:right;"
                  @click="sModal=true;"
                  v-if="(groupInfo.hostId === this.$store.state.userId) && !nowMeeting"
                  dark
                  color="green"
                >
                  회의 시작 
                </v-btn>
                <v-btn
                  @click="joinMeeting" style="float:right;"
                  v-if="(groupInfo.hostId != this.$store.state.userId) && ( nowMeeting || this.conferenceOn)"
                  dark
                  color="blue darken-2"
                  class="animate__animated animate__headShake"
                >
                  회의 참여
                </v-btn>
                  <v-dialog v-model="sModal" width="500px">
                    <v-card width="500px">
                      <v-card-title class="top">회의 시작하기</v-card-title>
                      <v-container>
                        <v-form v-on:submit.prevent="noop" ref="form" width="500px;" lazy-validation class="ml-2 mr-2">
                          <v-text-field v-model="meetingTitle" label="회의 명" @keypress.enter="startMeeting" required></v-text-field>
                          <v-card-actions>
                            <v-spacer></v-spacer>
                            <v-btn :disalbed="!!!meetingTitle" text color="error" class="mr-4" @click="startMeeting">회의 시작</v-btn>
                          </v-card-actions>
                        </v-form>
                      </v-container>
                    </v-card>
                  </v-dialog>
                
              </v-col>
            </v-row>
            <div style="margin-top:-30px; " >
              <v-row >
                  <v-col cols="11">
                      <v-row>
                      <v-badge dot color="rgb(0, 0, 0, 0)" style="width:95px;">
                        <v-list-item-avatar color="#F5F5F5" size="70">
                          <v-img :src="'https://joinmeeting.tk/images/'+groupInfo.profile"></v-img>
                        </v-list-item-avatar>
                      </v-badge>

                      <v-list-item-content style="width:400px;">
                        <v-list-item-title style="padding-top : 2px; font-size :20px;">{{ groupInfo.hostName }}</v-list-item-title>
                        <v-list-item-subtitle style="margin-left : 3px; font-size : 13px; color:rgba(0,0,0,0.6)">{{ groupInfo.hostId }}</v-list-item-subtitle>
                      </v-list-item-content>
                      </v-row>
                  </v-col>
              </v-row>
            </div>

            <!-- <h6>호스트 : {{ groupInfo }}</h6> -->
            <div style="height:60px; overflow-y:auto; margin-top : -8px;">
              <p style="font-size : 15px;">{{ groupInfo.groupIntro }}</p>
            </div>
          </div>
        </v-col>
      </v-row>
      <!-- <v-divider class="mb-10"></v-divider> -->

      <v-row class="GroupListBox">
        <v-col>
          <v-row>
            <v-col cols="8" class="pt-0">
              <p style="font-size: 24px;">그룹원</p>
            </v-col>
            <v-col cols="4" class="pt-0">
              <div v-if="groupInfo.hostId === this.$store.state.userId" style="float:right;">
                <InviteMember
                  :groupNo="groupInfo.groupNo"
                  :groupName="groupInfo.groupName"
                  :hostId="groupInfo.hostId"
                />
              </div>
            </v-col>
          </v-row>

          <div>
            <v-divider class="m-1"></v-divider>
            <v-col id="MemberListBox">
              <div v-if="members && members.length === 0">
                <v-icon color="rgb(52, 63, 87);" class="d-flex justify-center align-center mt-4" size="90">far fa-dizzy</v-icon>
                <h5 class="d-flex justify-center align-center mt-8">그룹원들이 없습니다</h5>
              </div>
              <div v-else>
                <v-card-text
                  v-for="memberInfo in members.slice(0,3)"
                  :key="memberInfo.id"
                  style="padding: 5px; margin-left:-20px; margin-top:-10px;"
                >
                  <MemberCard :userInfo="memberInfo" />
                </v-card-text>
              </div>

              <v-card-actions>
              </v-card-actions>
            </v-col>
          </div>
          <v-spacer></v-spacer>
          <div v-if="members && members.length !== 0">
            <GroupMembers
              :membersInfo="members"
              @refresh="getGroupMembers"
              :groupNo="groupInfo.groupNo"
              :hostId="groupInfo.hostId"
            />
          </div>
        </v-col>
      </v-row>

      <v-col>
        <v-row justify="end">
          <div class="mr-2" v-if="groupInfo.hostId === this.$store.state.userId">
            <v-btn dark color="red" @click="onModal=true" style="margin-top : 10px;">그룹 관리</v-btn>
            <v-dialog v-model="onModal" max-width="500px" height="100%">
              <EditGroup @close="onModal=false" :groupInfo="groupInfo" />
            </v-dialog>
          </div>
          <v-btn class="mr-2"
            dark
            color="red"
            @click="exitGroup"
            v-if="groupInfo.hostId !== this.$store.state.userId"
            style="margin-top : 20px;"
          >그룹 탈퇴</v-btn>
        </v-row>
      </v-col>
    </v-col>

    <v-spacer></v-spacer>
    <!-- 우측 캘린더 부분 -->
    <v-col cols="6">
      <GroupCalendar :meetingNoteInfo="meetingNoteInfo" />
    </v-col>
  </v-row>
</template>

<script>
import SERVER from '../../api/spring.js';
import axios from 'axios';

import SockJS from 'sockjs-client';
import Stomp from 'webstomp-client';

import MemberCard from './MemberCard.vue';
import GroupMembers from './GroupMembers.vue';
import InviteMember from './InviteMember.vue';
import EditGroup from './EditGroup.vue';
import GroupCalendar from './GroupCalendar.vue';

export default {
  name: 'group',
  components: {
    MemberCard,
    GroupMembers,
    InviteMember,
    GroupCalendar,
    EditGroup,
  },
  props: {
    groupInfo: Object,
    meetingNoteInfo: Array,
    conferenceAlert: Boolean,
    tmpGroupNo: Number,
    nowMeeting: Boolean,
  },
  data() {
    return {
      onModal: false,
      members: [],
      sock: null,
      ws: null,
      reconnect: 0,
      token: '',
      meetingNo: null,
      meetingTitle: this.$store.state.myName + '의 회의',
      sModal: false
    };
  },
  methods: {
    noop() {

    },
    exitGroup() {
      axios
        .delete(
          SERVER.URL +
            '/groupmember/delno/' +
            this.groupInfo.groupNo +
            '/' +
            this.$store.state.userId
        )
        .then((res) => {
          this.$router.push('/Home');
        })
        .catch((err) => console.log(err.response));
    },

    changeHasMeeting() {
      var tmp = null;
      axios
        .put(SERVER.URL + '/group/hasmeeting/' + this.groupInfo.groupNo)
        .then((res) => {
          // tmp = this.nowMeeting;
          tmp = res.data.hasMeeting;
        })
        .finally(() => {
          this.send(tmp);
        });
    },

    getGroupMembers() {
      axios
        .get(SERVER.URL + '/groupmember/getno/' + this.groupInfo.groupNo)
        .then((res) => {
          this.members = res.data.groupMembers;
        })
        .catch((err) => console.log(err.response));
    },

    getMyNickname() {
      for(var i = 0; i < this.members.length; i++) {
        if(this.$store.state.userId === this.members[i].id) {
          return this.members[i].nickname;
        }
        else {
          return this.$store.state.myName;
        }
      }
    },

    startMeeting() {
      this.changeHasMeeting();
      axios
        .post(SERVER.URL + '/meeting/add', {
          groupNo: this.groupInfo.groupNo,
          title: this.meetingTitle,
        })
        .then((res) => {
          this.meetingNo = res.data.meetingNo;
          this.sModal = false;
          this.$router.push({
            name: 'Conference',
            params: {
              roomId: this.groupInfo.roomId,
              hostId: this.groupInfo.hostId,
              groupNo: this.groupInfo.groupNo,
              groupName: this.groupInfo.groupName,
            },
            query: { meetingNo: this.meetingNo , nickname: this.$store.state.myName},
          });
        });
    },

    joinMeeting() {
      axios
        .get(
          SERVER.URL + '/meeting/get/currentmeeting/' + this.groupInfo.groupNo
        )
        .then((res) => {
          this.meetingNo = res.data.meetingNo;
          this.$router.push({
            name: 'Conference',
            params: {
              roomId: this.groupInfo.roomId,
              hostId: this.groupInfo.hostId,
              groupNo: this.groupInfo.groupNo,
              groupName: this.groupInfo.groupName,
            },
            query: { meetingNo: this.meetingNo, nickname: this.getMyNickname() },
          });
        });
    },

    send(tmp) {
      const msg = {
        meeting: tmp,
        groupNo: this.groupInfo.groupNo,
      };
      this.ws.send('/meeting', JSON.stringify(msg), {
        token: this.$store.state.accessToken,
      });
    },
  },

  created() {
    this.sock = new SockJS(SERVER.URL2);
    this.ws = Stomp.over(this.sock);
  },

  mounted() {
    this.sModal = false;
  },

  computed: {
    conferenceOn(){
      if(this.tmpGroupNo == this.groupInfo.groupNo){
        if(this.conferenceAlert){
          return true;
        }
      }
      return false;
    }
  },

  watch: {
    groupInfo() {
      this.getGroupMembers();
    },
    sModal(){
      this.meetingTitle = this.$store.state.myName + '의 회의';
    },
  },
};
</script>


<style scoped>
#conferenceBox {
  margin-top: 5px;
  margin-bottom: 20px;
  border-radius: 15px;
  height: 28vh;
  padding: 20px 30px 30px 30px;
  /* padding: 15px; */
  box-shadow: 1px 1px 2px 2px rgb(167, 167, 167);
}

.GroupListBox {
  height: 38vh;
  border-radius: 15px;
  padding: 15px;
  box-shadow: 1px 1px 2px 2px rgb(167, 167, 167);
}
#conferenceStatusBox{
 width: 100%;
 text-align: center;
}
.conferenceStatus {
  padding-top: 6px;
  font-size: 17px;
  border-radius: 10px;
  display: inline-block;
  height: 40px;
  text-align: center;
  width: 70%;
}


#onLineStatus {
  margin: 0;
}

#MemberListBox{
  overflow-y :auto;
}
</style>