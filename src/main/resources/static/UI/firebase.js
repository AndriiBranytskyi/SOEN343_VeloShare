// firebase.js
import { initializeApp } from "https://www.gstatic.com/firebasejs/11.0.1/firebase-app.js";
import { getDatabase }  from "https://www.gstatic.com/firebasejs/11.0.1/firebase-database.js";
import { getAuth }      from "https://www.gstatic.com/firebasejs/11.0.1/firebase-auth.js";

const firebaseConfig = {
    apiKey: "AIzaSyCjcYNh_WH9nNhGEFe_rfekgfah5dDB478",
    authDomain: "soen343-444c9.firebaseapp.com",
    databaseURL: "https://soen343-444c9-default-rtdb.firebaseio.com",
    projectId: "soen343-444c9",
    storageBucket: "soen343-444c9.firebasestorage.app",
    messagingSenderId: "431790784275",
    appId: "1:431790784275:web:29000d91ea966583be24e6",
    measurementId: "G-5G41LRZ9K2"
};

export const app  = initializeApp(firebaseConfig);
export const db   = getDatabase(app);
export const auth = getAuth(app);