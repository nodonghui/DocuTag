import com.google.genai.Client;
import com.google.genai.types.*;
import org.springframework.boot.SpringApplication;

public class testMain {
    public static void main(String[] args) {
        String apiKey = "AIzaSyAXKeAfVu2glF36af8iq3nridsHyR1dv5Y";
        String content = "안녕하세요 노동희 입니다 저는 노변 초 노변 중 오성고 경북대 나왔고요 삼남 칠녀 장손이며 해적왕이될 사나이입니다";
        System.out.println(summarize(content,apiKey));
        //ModelList(apiKey);
    }

    public static void ModelList(String apiKey) {
        try (Client client = Client.builder().apiKey(apiKey).build()) {

            System.out.println("List of models that support generateContent:\n");
            for (Model m : client.models.list(null)) {
                System.out.println(m.name());
            }



        } catch (Exception e) {
            System.out.println("모델 목록 조회 실패: " + e.getMessage());
        }
    }

    public static String summarize(String content,String apiKey) {

        try (Client client = Client.builder().apiKey(apiKey).build()) {

            GenerateContentConfig config = GenerateContentConfig.builder()
                    .systemInstruction(
                            Content.fromParts(Part.fromText("텍스트를 받으면 문단 형식으로 정리해줘.")))
                    .build();

            GenerateContentResponse response =
                    client.models.generateContent("gemini-2.5-flash", content, config);

            return response == null ? "default" : response.text();

        } catch (Exception e) {
            System.out.println("Gemini API 호출 실패: " + e.getMessage());
            return "default";
        }
    }

}

