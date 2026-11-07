function DesignReview() {
  return (
    <React.Fragment>
      <DesignCanvas minScale={0.2} maxScale={2.5}>
        <DCSection id="journal" title="A · 温柔手帐" subtitle="纸张、手写字与柔和印记｜更像一本每天陪你翻开的私人手帐" gap={56}>
          <DCArtboard id="journal-home" label="A1 · 今日打卡（可交互）" width={393} height={852} style={{ borderRadius: 38, background: "transparent", boxShadow: "none" }}>
            <DirectionPrototype theme="journal" initialScreen="home" />
          </DCArtboard>
          <DCArtboard id="journal-history" label="A2 · 坚持足迹" width={393} height={852} style={{ borderRadius: 38, background: "transparent", boxShadow: "none" }}>
            <DirectionPrototype theme="journal" initialScreen="history" />
          </DCArtboard>
        </DCSection>
        <DCSection id="grow" title="B · 轻盈生长" subtitle="植物、生长与轻盈色彩｜把每次打卡变成看得见的一片新叶" gap={56}>
          <DCArtboard id="grow-home" label="B1 · 今日打卡（可交互）" width={393} height={852} style={{ borderRadius: 38, background: "transparent", boxShadow: "none" }}>
            <DirectionPrototype theme="grow" initialScreen="home" />
          </DCArtboard>
          <DCArtboard id="grow-history" label="B2 · 坚持足迹" width={393} height={852} style={{ borderRadius: 38, background: "transparent", boxShadow: "none" }}>
            <DirectionPrototype theme="grow" initialScreen="history" />
          </DCArtboard>
        </DCSection>
        <DCSection id="minimal" title="C · 安静秩序" subtitle="数字、网格与清晰秩序｜首页效率最高，情绪价值藏在细节里" gap={56}>
          <DCArtboard id="minimal-home" label="C1 · 今日打卡（可交互）" width={393} height={852} style={{ borderRadius: 38, background: "transparent", boxShadow: "none" }}>
            <DirectionPrototype theme="minimal" initialScreen="home" />
          </DCArtboard>
          <DCArtboard id="minimal-history" label="C2 · 坚持足迹" width={393} height={852} style={{ borderRadius: 38, background: "transparent", boxShadow: "none" }}>
            <DirectionPrototype theme="minimal" initialScreen="history" />
          </DCArtboard>
        </DCSection>
      </DesignCanvas>
      <div className="canvas-guide"><span className="canvas-guide-dot"></span>拖动空白处平移 · 滚轮缩放 · 点击画板标题放大 · 圆圈可打卡，＋可新建习惯</div>
    </React.Fragment>
  );
}

ReactDOM.createRoot(document.getElementById("root")).render(<DesignReview />);
