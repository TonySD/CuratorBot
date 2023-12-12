package com.example.curatorBot.API.parsers;

import com.example.curatorBot.API.dto.homeworks.HomeworkDTO;
import com.example.curatorBot.API.validators.ApiAuthValidator;
import com.example.curatorBot.API.dto.homeworks.HomeworkPages;
import com.example.curatorBot.API.dto.homeworks.ParsedHomework;
import com.example.curatorBot.configParser;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
public class ApiParser {
    private int homeworks;
    private final ApiAuthValidator apiAuthValidator;

    public ApiParser() {
        apiAuthValidator = new ApiAuthValidator();
        getHWInfo();
    }

    public Optional<ParsedHomework> getSelectedHW(HomeworkDTO homeworkDTO) {
        Document doc;
        try {
            doc = Jsoup.connect(homeworkDTO.getUrl())
                    .cookies(apiAuthValidator.getAuth())
                    .get();
            String student_name = doc
                    .select("#mainForm > div:nth-child(1) > div > div > div:nth-child(2) > div > div.row > div:nth-child(1) > input")
                    .first()
                    .attr("value");
            String lesson_name = doc
                    .select("#mainForm > div:nth-child(1) > div > div > div:nth-child(2) > div > div.row > div:nth-child(2) > div:nth-child(1) > small > b")
                    .first().text();
            String[] mark = doc
                    .select("#mainForm > div:nth-child(1) > div > div > div:nth-child(2) > div > div.row > div:nth-child(6) > div:nth-child(1)")
                    .first().text()
                    .split(": ")[1]
                    .split("/");
            int student_mark = Integer.parseInt(mark[0]);
            int max_mark = Integer.parseInt(mark[1]);
            log.debug("Homework was parsed successfully");

            return Optional.of(new ParsedHomework(
                    student_name,
                    lesson_name,
                    student_mark,
                    max_mark
            ));
        } catch (IOException | NullPointerException exception) {
            log.error(exception.getMessage());
        }

        return Optional.empty();
    }

    private Optional<HomeworkPages> getHWInfo() {
        Document doc;
        try {
            doc = Jsoup.connect(
                    String.format(
                            configParser.getProperty("api.homeworks_url"),
                            1
                    ))
                    .cookies(apiAuthValidator.getAuth())
                    .get();
            Element data = doc.select("#example2_info").first();
            assert data != null;
            String[] result = data.select("div").text().split(" ");
            result[4] = result[4].substring(0, result[4].length() - 1);
            homeworks = Integer.parseInt(result[6]);
            return Optional.of(new HomeworkPages(
                    homeworks,
                    Integer.parseInt(result[4])
            ));
        } catch (IOException | NullPointerException exception) {
            log.error(exception.getMessage());
        }
        return Optional.empty();

    }

    private List<ParsedHomework> getHWsFromPage(int number) {
        ArrayList<ParsedHomework> result = new ArrayList<>();
        String url = String.format(
                configParser.getProperty("api.homeworks_url"),
                number
        );
        try {
            Document doc = Jsoup.connect(url)
                    .cookies(apiAuthValidator.getAuth())
                    .get();
            Elements HWElements = doc.select("html body.sidebar-mini.layout-fixed div.wrapper div.content-wrapper section.content div.container-fluid div.container-fluid div.row div.col-12 div.card div.card-body div#example2_wrapper.dataTables_wrapper.dt-bootstrap4 div.row div.col-sm-12 table#example2.table.table-bordered.table-hover.dataTable.dtr-inline tbody tr.odd td a.btn.btn-xs.bg-purple");
            log.trace("Page number {} : Got {} homework urls", number, HWElements.size());
            for (Element element : HWElements) {
                String homeworkUrl = element.attr("href");
                result.add(
                        getSelectedHW(
                            new HomeworkDTO(homeworkUrl)
                        ).orElseThrow()
                );
            }
        } catch (Exception e) {
            log.error("Error getting homeworks from page {} :\n{}", number, e);
        }
        return result;
    }

    private int getAmountNewHomeworks() {
        Optional<HomeworkPages> result = getHWInfo();
        if (result.isEmpty()) return 0;
        return result.get().amount_of_HWS() - homeworks;
    }

    private List<ParsedHomework> getNewHomeworks() {
        int amountNewHomeworks = getAmountNewHomeworks();
        if (amountNewHomeworks == 0) return new ArrayList<>();
        HomeworkPages homeworkPages = getHWInfo().orElseThrow();
        int pagesToParse = Math.ceilDiv(amountNewHomeworks, homeworkPages.HWs_on_page());
        log.trace("{} pages to parse", pagesToParse);
        ArrayList<ParsedHomework> result = new ArrayList<>();

        for (int pageNumber = 1; pageNumber <= pagesToParse; ++pageNumber) {
            result.addAll(getHWsFromPage(pageNumber));
        }

        return result;
    }
}
