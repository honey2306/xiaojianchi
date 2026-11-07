const initialHabits = [
  {
    id: "reading", name: "读书", icon: "book", color: "#b87452", done: true,
    total: 36, current: 6, longest: 12, reminder: "20:30",
    note: "读了 10 分钟，今天也做到了。",
    marks: [1,2,3,4,6,7,8,9,10,11,13,14,15,16,17,18],
  },
  {
    id: "movement", name: "运动", icon: "move", color: "#5c8371", done: true,
    total: 12, current: 4, longest: 5, reminder: "19:00",
    note: "出门走了 20 分钟，身体轻松了一点。",
    marks: [2,4,6,8,10,12,13,14,15,16,17,18],
  },
  {
    id: "words", name: "背单词", icon: "language", color: "#7b6f9d", done: false,
    total: 24, current: 3, longest: 8, reminder: "21:00",
    note: "今天复习了旧词，比一味学新词更踏实。",
    marks: [1,3,5,7,9,11,12,14,15,16,17],
  },
  {
    id: "meditation", name: "冥想", icon: "lotus", color: "#b2934f", done: false,
    total: 18, current: 5, longest: 7, reminder: "22:00",
    note: "给自己留了安静的五分钟。",
    marks: [2,3,5,8,9,11,14,15,16,17],
  },
];

const monthDays = [null, ...Array.from({ length: 30 }, (_, index) => index + 1)];
const habitColors = ["#b87452", "#5c8371", "#7b6f9d", "#b2934f", "#4e7e91"];

Object.assign(window, { initialHabits, monthDays, habitColors });
