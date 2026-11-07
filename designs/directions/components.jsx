function PhoneShell({ theme, screenLabel, children }) {
  return (
    <div className={`phone theme-${theme}`} data-screen-label={screenLabel}>
      <div className="phone-status">
        <span>9:41</span>
        <div className="phone-status-icons">
          <Icon name="wifi" size={13} strokeWidth={2} />
          <svg width="16" height="11" viewBox="0 0 16 11" fill="currentColor" aria-hidden="true"><rect x="0" y="7" width="3" height="4" rx="1"/><rect x="4.3" y="5" width="3" height="6" rx="1"/><rect x="8.6" y="2.5" width="3" height="8.5" rx="1"/><rect x="12.8" y="0" width="3" height="11" rx="1"/></svg>
          <svg width="20" height="11" viewBox="0 0 22 12" fill="none" stroke="currentColor" aria-hidden="true"><rect x=".5" y=".5" width="18" height="11" rx="3" opacity=".45"/><path d="M20 4v4" strokeLinecap="round"/><rect x="2.5" y="2.5" width="13" height="7" rx="1.5" fill="currentColor" stroke="none"/></svg>
        </div>
      </div>
      {children}
      <div className="phone-gesture"><span></span></div>
    </div>
  );
}

function IconButton({ icon, label, onClick, className = "" }) {
  return <button className={`icon-button ${className}`} aria-label={label} title={label} onClick={onClick}><Icon name={icon} size={20} /></button>;
}

function BottomNav({ active, onChange }) {
  return (
    <nav className="bottom-nav" aria-label="主导航">
      <button className={`nav-item ${active === "home" ? "active" : ""}`} onClick={() => onChange("home")} aria-current={active === "home" ? "page" : undefined}>
        <Icon name="home" size={20} />
        <span>今天</span>
      </button>
      <button className={`nav-item ${active === "history" ? "active" : ""}`} onClick={() => onChange("history")} aria-current={active === "history" ? "page" : undefined}>
        <Icon name="calendar" size={19} />
        <span>足迹</span>
      </button>
    </nav>
  );
}

function HabitCard({ habit, onToggle, onOpen }) {
  return (
    <article className={`habit-card ${habit.done ? "done" : ""}`} style={{ "--habit": habit.color }}>
      <button className="habit-main" onClick={() => onOpen(habit.id)} aria-label={`查看${habit.name}的坚持足迹`}>
        <span className="habit-symbol"><Icon name={habit.icon} size={23} /></span>
        <span className="habit-copy">
          <span className="habit-name">{habit.name}</span>
          <span className="habit-meta">{habit.done ? `今天已打卡 · 累计 ${habit.total} 天` : `当前连续 ${habit.current} 天 · 今天等你`}</span>
        </span>
      </button>
      <button className={`check-button ${habit.done ? "checked" : ""}`} onClick={() => onToggle(habit.id)} aria-label={habit.done ? `撤销${habit.name}今日打卡` : `完成${habit.name}今日打卡`} aria-pressed={habit.done}>
        <Icon name="check" size={22} strokeWidth={habit.done ? 2.2 : 1.4} />
      </button>
    </article>
  );
}

function StatsPanel({ habit }) {
  return (
    <section className="stats-panel" aria-label={`${habit.name}坚持统计`}>
      <div className="stats-total">
        <span className="stats-total-label">累计坚持</span>
        <div className="stats-total-value"><strong>{habit.total}</strong><span>天</span></div>
      </div>
      <div className="stats-secondary">
        <div className="stat-small"><span>当前连续</span><strong>{habit.current} 天</strong></div>
        <div className="stat-small"><span>最长连续</span><strong>{habit.longest} 天</strong></div>
      </div>
    </section>
  );
}

function HabitFilters({ habits, selectedId, onSelect }) {
  return (
    <div className="filter-row" role="tablist" aria-label="选择习惯">
      {habits.slice(0, 4).map((habit) => (
        <button key={habit.id} role="tab" aria-selected={selectedId === habit.id} className={`filter-chip ${selectedId === habit.id ? "active" : ""}`} onClick={() => onSelect(habit.id)}>{habit.name}</button>
      ))}
    </div>
  );
}

function MonthCalendar({ habit }) {
  const marked = new Set(habit.marks);
  return (
    <section className="calendar-card" aria-label={`${habit.name}四月打卡日历`}>
      <div className="calendar-heading"><strong>2025 年 4 月</strong><span>每一天，都算数</span></div>
      <div className="calendar-weekdays">{["一","二","三","四","五","六","日"].map((day) => <span key={day}>{day}</span>)}</div>
      <div className="calendar-grid">
        {monthDays.map((day, index) => day === null ? <span className="calendar-day empty" key={`empty-${index}`}></span> : (
          <div key={day} className={`calendar-day ${marked.has(day) ? "marked" : ""} ${day === 18 ? "today" : ""} ${day > 18 ? "future" : ""}`} aria-label={`4月${day}日${marked.has(day) ? "，已打卡" : day > 18 ? "，还未到来" : "，未打卡"}`}>
            <span>{day}</span>
            {marked.has(day) && <span className="day-check"><Icon name="check" size={9} strokeWidth={2.5} /></span>}
          </div>
        ))}
      </div>
    </section>
  );
}

function AddHabitSheet({ onClose, onSave }) {
  const [name, setName] = React.useState("");
  const [icon, setIcon] = React.useState("book");
  const [color, setColor] = React.useState(habitColors[0]);
  const [reminder, setReminder] = React.useState(true);
  const submit = (event) => {
    event.preventDefault();
    if (!name.trim()) return;
    onSave({ name: name.trim(), icon, color, reminder: reminder ? "20:30" : "" });
  };
  return (
    <div className="sheet-backdrop" onClick={onClose}>
      <form className="bottom-sheet" onClick={(event) => event.stopPropagation()} onSubmit={submit} aria-label="新建习惯">
        <div className="sheet-handle"></div>
        <div className="sheet-header"><h2>想坚持什么？</h2><IconButton icon="close" label="关闭" onClick={onClose} /></div>
        <div className="form-field">
          <label htmlFor="habit-name">习惯名称</label>
          <input id="habit-name" className="text-input" value={name} onChange={(event) => setName(event.target.value)} placeholder="例如：每天写一点" maxLength={12} autoFocus />
        </div>
        <div className="form-field">
          <span className="form-label">选择一个图标</span>
          <div className="icon-options">
            {["book","move","language","lotus","spark"].map((item) => <button type="button" key={item} className={`icon-option ${icon === item ? "selected" : ""}`} onClick={() => setIcon(item)} aria-label={`选择${item}图标`} aria-pressed={icon === item}><Icon name={item} size={22} /></button>)}
          </div>
        </div>
        <div className="form-field">
          <span className="form-label">一点属于自己的颜色</span>
          <div className="color-options">
            {habitColors.map((item) => <button type="button" key={item} className={`color-option ${color === item ? "selected" : ""}`} style={{ "--swatch": item }} onClick={() => setColor(item)} aria-label={`选择颜色${item}`} aria-pressed={color === item}></button>)}
          </div>
        </div>
        <div className="reminder-row">
          <div className="reminder-copy"><strong>每日温和提醒</strong><span>{reminder ? "20:30 · 今天想给自己留一点时间吗？" : "不提醒，按自己的节奏来"}</span></div>
          <button className={`switch ${reminder ? "on" : ""}`} type="button" role="switch" aria-checked={reminder} aria-label="每日提醒" onClick={() => setReminder(!reminder)}></button>
        </div>
        <button className="primary-button" type="submit" disabled={!name.trim()}>开始坚持</button>
      </form>
    </div>
  );
}

function NoteSheet({ habit, onClose, onSave }) {
  const [note, setNote] = React.useState(habit.note || "");
  return (
    <div className="sheet-backdrop" onClick={onClose}>
      <form className="bottom-sheet" onClick={(event) => event.stopPropagation()} onSubmit={(event) => { event.preventDefault(); onSave(note.trim()); }} aria-label="记录今日感受">
        <div className="sheet-handle"></div>
        <div className="sheet-header"><h2>今天也做到了</h2><IconButton icon="close" label="关闭" onClick={onClose} /></div>
        <div className="form-field">
          <label htmlFor="habit-note">给「{habit.name}」留一句话，也可以不写</label>
          <textarea id="habit-note" className="note-input" value={note} onChange={(event) => setNote(event.target.value)} placeholder="今天只做了十分钟，但没有放弃。" maxLength={80} autoFocus />
        </div>
        <button className="primary-button" type="submit">留住今天</button>
      </form>
    </div>
  );
}

function CheckToast({ habit, onNote, onClose }) {
  return (
    <div className="toast" role="status">
      <Icon name="check" size={17} />
      <span>{habit.done ? "今天也做到了，明天见。" : "新习惯已加入，从一次开始。"}</span>
      {habit.done && <button onClick={() => onNote(habit.id)}>写一句</button>}
      <button onClick={onClose} aria-label="关闭提示"><Icon name="close" size={14} /></button>
    </div>
  );
}

function useHabitPrototype(initialScreen) {
  const [screen, setScreen] = React.useState(initialScreen);
  const [habits, setHabits] = React.useState(() => initialHabits.map((habit) => ({ ...habit, marks: [...habit.marks] })));
  const [selectedId, setSelectedId] = React.useState("reading");
  const [sheet, setSheet] = React.useState(null);
  const [toastId, setToastId] = React.useState(null);

  React.useEffect(() => {
    if (!toastId) return undefined;
    const timer = setTimeout(() => setToastId(null), 6000);
    return () => clearTimeout(timer);
  }, [toastId]);

  const toggleHabit = (id) => {
    setHabits((current) => current.map((habit) => {
      if (habit.id !== id) return habit;
      const done = !habit.done;
      return {
        ...habit, done,
        total: Math.max(0, habit.total + (done ? 1 : -1)),
        current: Math.max(0, habit.current + (done ? 1 : -1)),
        marks: done ? [...new Set([...habit.marks, 18])] : habit.marks.filter((day) => day !== 18),
      };
    }));
    setToastId(id);
  };
  const openHistory = (id) => { if (id) setSelectedId(id); setScreen("history"); };
  const addHabit = (draft) => {
    const id = `habit-${Date.now()}`;
    setHabits((current) => [...current, { ...draft, id, done: false, total: 0, current: 0, longest: 0, note: "从今天开始，给自己留下一点痕迹。", marks: [] }]);
    setSheet(null);
    setToastId(id);
  };
  const saveNote = (id, note) => {
    setHabits((current) => current.map((habit) => habit.id === id ? { ...habit, note } : habit));
    setSheet(null);
  };
  const selected = habits.find((habit) => habit.id === selectedId) || habits[0];
  const toastHabit = habits.find((habit) => habit.id === toastId);
  const completed = habits.filter((habit) => habit.done).length;
  return {
    screen, setScreen, habits, selectedId, setSelectedId, selected, completed,
    sheet, setSheet, toastId, setToastId, toastHabit,
    toggleHabit, openHistory, addHabit, saveNote,
  };
}

Object.assign(window, {
  PhoneShell, IconButton, BottomNav, HabitCard, StatsPanel, HabitFilters,
  MonthCalendar, AddHabitSheet, NoteSheet, CheckToast, useHabitPrototype,
});
