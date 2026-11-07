function JournalHome({ prototype }) {
  const p = prototype;
  return (
    <PhoneShell theme="journal" screenLabel="A 温柔手帐 · 今日打卡">
      <div className="app-screen">
        <main className="app-content">
          <header className="app-topbar">
            <div className="journal-brand">小坚持</div>
            <div className="topbar-actions"><IconButton icon="plus" label="新建习惯" onClick={() => p.setSheet({ type: "add" })} /><IconButton icon="settings" label="设置" /></div>
          </header>
          <section className="journal-hero">
            <div>
              <div className="journal-date">4 月 18 日 · 星期五</div>
              <h1>不必做得很多，<br />今天做一点就好。</h1>
              <div className="journal-underline"></div>
            </div>
            <div className="journal-progress"><strong>{p.completed}/{p.habits.length}</strong><span>今日已完成</span></div>
          </section>
          <div className="section-label"><h2>今天的小坚持</h2><span>按自己的节奏来</span></div>
          <section className="habit-list" aria-label="今日习惯">
            {p.habits.map((habit) => <HabitCard key={habit.id} habit={habit} onToggle={p.toggleHabit} onOpen={p.openHistory} />)}
          </section>
          <div className="journal-quote">每一个普通的日子，<br />都因为你的一点坚持而不同。</div>
        </main>
        <BottomNav active="home" onChange={p.setScreen} />
        {p.toastHabit && <CheckToast habit={p.toastHabit} onNote={(id) => p.setSheet({ type: "note", id })} onClose={() => p.setToastId(null)} />}
        {p.sheet?.type === "add" && <AddHabitSheet onClose={() => p.setSheet(null)} onSave={p.addHabit} />}
        {p.sheet?.type === "note" && <NoteSheet habit={p.habits.find((habit) => habit.id === p.sheet.id)} onClose={() => p.setSheet(null)} onSave={(note) => p.saveNote(p.sheet.id, note)} />}
      </div>
    </PhoneShell>
  );
}

function JournalHistory({ prototype }) {
  const p = prototype;
  return (
    <PhoneShell theme="journal" screenLabel="A 温柔手帐 · 坚持足迹">
      <div className="app-screen">
        <main className="app-content">
          <header className="app-topbar"><div className="journal-brand">小坚持</div><div className="topbar-actions"><IconButton icon="more" label={`${p.selected.name}更多操作`} /></div></header>
          <div className="journal-history-title"><h1>我的坚持</h1><p>那些没有放弃的日子，都被好好收藏。</p></div>
          <HabitFilters habits={p.habits} selectedId={p.selectedId} onSelect={p.setSelectedId} />
          <StatsPanel habit={p.selected} />
          <MonthCalendar habit={p.selected} />
          <div className="history-note"><div className="history-note-date">4 月 18 日 · 今天的小记录</div><div className="history-note-text">“{p.selected.note}”</div></div>
          <div className="journal-emotion">中断不会抹去你已经走过的 {p.selected.total} 天。</div>
        </main>
        <BottomNav active="history" onChange={p.setScreen} />
      </div>
    </PhoneShell>
  );
}

function GrowGarden({ completed, total }) {
  const leaves = [
    { left: 43, bottom: 82, rotate: "12deg", color: "#7fa072" },
    { left: 82, bottom: 68, rotate: "168deg", color: "#5f8d67" },
    { left: 45, bottom: 55, rotate: "-8deg", color: "#96ac73" },
    { left: 82, bottom: 94, rotate: "158deg", color: "#749761" },
  ];
  return (
    <div className="garden-visual" aria-label={`${total}个习惯中已完成${completed}个，植物随之生长`}>
      <div className="garden-stem"></div>
      {leaves.map((leaf, index) => <span key={index} className={`garden-leaf ${index < completed ? "alive" : ""}`} style={{ left: leaf.left, bottom: leaf.bottom, "--leaf-rotate": leaf.rotate, "--leaf-color": leaf.color }}></span>)}
      <div className="garden-pot"></div>
    </div>
  );
}

function GrowHome({ prototype }) {
  const p = prototype;
  return (
    <PhoneShell theme="grow" screenLabel="B 轻盈生长 · 今日打卡">
      <div className="app-screen">
        <div className="grow-orb"></div>
        <main className="app-content">
          <header className="app-topbar">
            <div className="grow-brand"><span className="grow-brand-mark"><Icon name="leaf" size={18} /></span>小坚持</div>
            <div className="topbar-actions"><IconButton icon="plus" label="新建习惯" onClick={() => p.setSheet({ type: "add" })} /><IconButton icon="settings" label="设置" /></div>
          </header>
          <section className="grow-hero">
            <div className="grow-hero-copy">
              <div className="grow-date">4 月 18 日 · 星期五</div>
              <h1>小小的坚持，<br />正在慢慢生长。</h1>
              <div className="grow-progress-copy">今天已点亮 <strong>{p.completed}</strong> / {p.habits.length}</div>
            </div>
            <GrowGarden completed={p.completed} total={p.habits.length} />
          </section>
          <div className="grow-section"><h2>今天，照顾一下它们</h2><span>{p.habits.length - p.completed} 个待完成</span></div>
          <section className="habit-list" aria-label="今日习惯">
            {p.habits.map((habit) => <HabitCard key={habit.id} habit={habit} onToggle={p.toggleHabit} onOpen={p.openHistory} />)}
          </section>
          <div className="grow-quote"><Icon name="leaf" size={14} />不用每天茂盛，只要继续生长。</div>
        </main>
        <BottomNav active="home" onChange={p.setScreen} />
        {p.toastHabit && <CheckToast habit={p.toastHabit} onNote={(id) => p.setSheet({ type: "note", id })} onClose={() => p.setToastId(null)} />}
        {p.sheet?.type === "add" && <AddHabitSheet onClose={() => p.setSheet(null)} onSave={p.addHabit} />}
        {p.sheet?.type === "note" && <NoteSheet habit={p.habits.find((habit) => habit.id === p.sheet.id)} onClose={() => p.setSheet(null)} onSave={(note) => p.saveNote(p.sheet.id, note)} />}
      </div>
    </PhoneShell>
  );
}

function GrowHistory({ prototype }) {
  const p = prototype;
  return (
    <PhoneShell theme="grow" screenLabel="B 轻盈生长 · 坚持足迹">
      <div className="app-screen">
        <main className="app-content">
          <header className="app-topbar"><div className="grow-brand"><span className="grow-brand-mark"><Icon name="leaf" size={18} /></span>小坚持</div><IconButton icon="more" label={`${p.selected.name}更多操作`} /></header>
          <div className="grow-history-head"><h1>生长的痕迹</h1><p>每一片新叶，都是认真生活过的证据。</p></div>
          <HabitFilters habits={p.habits} selectedId={p.selectedId} onSelect={p.setSelectedId} />
          <StatsPanel habit={p.selected} />
          <MonthCalendar habit={p.selected} />
          <div className="history-note"><div className="history-note-date">4 月 18 日 · 今天的小记录</div><div className="history-note-text">{p.selected.note}</div></div>
          <div className="grow-emotion"><Icon name="leaf" size={12} /> 已经留下的 {p.selected.total} 天，永远属于你。</div>
        </main>
        <BottomNav active="history" onChange={p.setScreen} />
      </div>
    </PhoneShell>
  );
}

function MinimalHome({ prototype }) {
  const p = prototype;
  return (
    <PhoneShell theme="minimal" screenLabel="C 安静秩序 · 今日打卡">
      <div className="app-screen">
        <main className="app-content">
          <header className="app-topbar">
            <div className="minimal-brand"><strong>小坚持</strong><span>SMALL STEPS</span></div>
            <div className="topbar-actions"><IconButton icon="plus" label="新建习惯" onClick={() => p.setSheet({ type: "add" })} /><IconButton icon="settings" label="设置" /></div>
          </header>
          <section className="minimal-hero">
            <div className="minimal-date">FRI. 18 APRIL 2025</div>
            <div className="minimal-progress-row"><div className="minimal-big-progress"><strong>{p.completed}</strong><span>/ {p.habits.length}</span></div><div className="minimal-hero-note">今天已完成<br />不追赶，只继续</div></div>
            <div className="progress-track"><span style={{ width: `${p.habits.length ? (p.completed / p.habits.length) * 100 : 0}%` }}></span></div>
          </section>
          <div className="minimal-section-title"><h2>今日习惯</h2><span>TODAY</span></div>
          <section className="habit-list" aria-label="今日习惯">
            {p.habits.map((habit) => <HabitCard key={habit.id} habit={habit} onToggle={p.toggleHabit} onOpen={p.openHistory} />)}
          </section>
          <div className="minimal-insight">已完成 {p.completed} 件。做到一点，就记录一点。</div>
        </main>
        <BottomNav active="home" onChange={p.setScreen} />
        {p.toastHabit && <CheckToast habit={p.toastHabit} onNote={(id) => p.setSheet({ type: "note", id })} onClose={() => p.setToastId(null)} />}
        {p.sheet?.type === "add" && <AddHabitSheet onClose={() => p.setSheet(null)} onSave={p.addHabit} />}
        {p.sheet?.type === "note" && <NoteSheet habit={p.habits.find((habit) => habit.id === p.sheet.id)} onClose={() => p.setSheet(null)} onSave={(note) => p.saveNote(p.sheet.id, note)} />}
      </div>
    </PhoneShell>
  );
}

function MinimalHistory({ prototype }) {
  const p = prototype;
  return (
    <PhoneShell theme="minimal" screenLabel="C 安静秩序 · 坚持足迹">
      <div className="app-screen">
        <main className="app-content">
          <header className="app-topbar"><div className="minimal-brand"><strong>小坚持</strong><span>SMALL STEPS</span></div><IconButton icon="more" label={`${p.selected.name}更多操作`} /></header>
          <div className="minimal-history-head"><span>YOUR FOOTPRINT</span><h1>坚持足迹</h1></div>
          <HabitFilters habits={p.habits} selectedId={p.selectedId} onSelect={p.setSelectedId} />
          <StatsPanel habit={p.selected} />
          <MonthCalendar habit={p.selected} />
          <div className="history-note"><div className="history-note-date">18 APR · TODAY</div><div className="history-note-text">{p.selected.note}</div></div>
          <div className="minimal-emotion">累计 {p.selected.total} 天 · 过去每一步都有效</div>
        </main>
        <BottomNav active="history" onChange={p.setScreen} />
      </div>
    </PhoneShell>
  );
}

function DirectionPrototype({ theme, initialScreen }) {
  const prototype = useHabitPrototype(initialScreen);
  if (theme === "journal") return prototype.screen === "home" ? <JournalHome prototype={prototype} /> : <JournalHistory prototype={prototype} />;
  if (theme === "grow") return prototype.screen === "home" ? <GrowHome prototype={prototype} /> : <GrowHistory prototype={prototype} />;
  return prototype.screen === "home" ? <MinimalHome prototype={prototype} /> : <MinimalHistory prototype={prototype} />;
}

Object.assign(window, { DirectionPrototype });
