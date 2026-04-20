package docuTag.domain.ai;

import lombok.Getter;

@Getter
public enum SummaryMode {

    서론본론결론("텍스트를 서론, 본론, 결론 3단계로 나눠서 정리해줘.\n[서론] 주제 소개 및 배경\n[본론] 핵심 내용과 근거\n[결론] 요약 및 마무리"),
    MECE("텍스트를 MECE 구조로 정리해줘.\n항목이 서로 겹치지 않고 빠짐없이 분류되도록 해줘.\n각 항목은 하위 항목으로 나눠줘."),
    피라미드("텍스트를 피라미드 구조로 정리해줘.\n핵심 결론을 먼저 쓰고, 그 아래에 근거들을 나열해줘."),
    PAS("텍스트를 문제-원인-해결 구조로 정리해줘.\n[문제] 어떤 문제인지\n[원인] 왜 발생했는지\n[해결] 어떻게 해결할지"),
    타임라인("텍스트를 시간 순서대로 정리해줘.\n각 단계를 1단계, 2단계... 형식으로 순서대로 나열해줘."),
    기본("텍스트를 글 구조를 그대로 두고 깔끔하게 정리만 해줘");

    private final String instruction;

    SummaryMode(String instruction) {
        this.instruction = instruction;
    }
}
