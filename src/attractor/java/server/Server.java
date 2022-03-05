package attractor.java.server;

import attractor.java.basicServer.*;
import attractor.java.dataModels.Day;
import attractor.java.dataModels.Task;
import com.sun.net.httpserver.HttpExchange;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Server extends BasicServer {
    private final static Configuration freemarker = initFreeMarker();

    public Server(String host, int port) throws IOException {
        super(host, port);
        registerGet("/calendar", this::freemarkerCalendarHandler);
        registerGet("/day", this::freemarkerDayHandler);
        registerGet("/delete", this::deletionHandler);
        registerGet("/addTask", this::addTaskHandlerGet);
        registerPost("/addTask", this::addTaskHandlerPost);

    }

    private static Configuration initFreeMarker() {
        try {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
            // путь к каталогу в котором у нас хранятся шаблоны
            // это может быть совершенно другой путь, чем тот, откуда сервер берёт файлы
            // которые отправляет пользователю
            cfg.setDirectoryForTemplateLoading(new File("data"));

            // прочие стандартные настройки о них читать тут
            // https://freemarker.apache.org/docs/pgui_quickstart_createconfiguration.html
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            cfg.setLogTemplateExceptions(false);
            cfg.setWrapUncheckedExceptions(true);
            cfg.setFallbackOnNullLoopVariable(false);
            return cfg;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void renderTemplate(HttpExchange exchange, String templateFile, Object dataModel) {
        try {
            // загружаем шаблон из файла по имени.
            // шаблон должен находится по пути, указанном в конфигурации
            Template temp = freemarker.getTemplate(templateFile);

            // freemarker записывает преобразованный шаблон в объект класса writer
            // а наш сервер отправляет клиенту массивы байт
            // по этому нам надо сделать "мост" между этими двумя системами

            // создаём поток который сохраняет всё, что в него будет записано в байтовый массив
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // создаём объект, который умеет писать в поток и который подходит для freemarker
            try (OutputStreamWriter writer = new OutputStreamWriter(stream)) {

                // обрабатываем шаблон заполняя его данными из модели
                // и записываем результат в объект "записи"
                temp.process(dataModel, writer);
                writer.flush();

                // получаем байтовый поток
                var data = stream.toByteArray();

                // отправляем результат клиенту
                sendByteData(exchange, ResponseCodes.OK, ContentType.TEXT_HTML, data);
            }
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
    }

    private void registerPost(String route, RouteHandler handler) {
        getRoutes().put("POST " + route, handler);
    }

    private Map<String, String> parseData(HttpExchange exchange) {
        String raw = getBody(exchange);
        return Utils.parseUrlEncoded(raw, "&");
    }

    protected String getBody(HttpExchange exchange) {
        InputStream input = exchange.getRequestBody();
        Charset utf8 = StandardCharsets.UTF_8;
        InputStreamReader isr = new InputStreamReader(input, utf8);
        try (BufferedReader reader = new BufferedReader(isr)) {
            return reader.lines().collect(Collectors.joining(""));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }


    private void addTaskHandlerPost(HttpExchange exchange) {
        var parsedData = parseData(exchange);
        var name = parsedData.get("name");
        var desc = parsedData.get("desc");
        var type = Integer.parseInt(parsedData.get("type"));
        int idDay = getId(exchange);

        Task task = new Task(-1, name, type, desc, idDay);
        JsonSerializer.writeTask(task);
        redirect303(exchange, "/calendar");
    }

    protected void redirect303(HttpExchange exchange, String path) {
        try {
            exchange.getResponseHeaders().add("Location", path);
            exchange.sendResponseHeaders(303, 0);
            exchange.getResponseBody().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addTaskHandlerGet(HttpExchange exchange) {
        int idDay = getId(exchange);

        Map<String, List<Task>> allInOne = new HashMap<>();
        List<Task> idDayArr = new ArrayList<>();
        idDayArr.add(new Task(0, "none", 0, "none", idDay));
        allInOne.put("idDay", idDayArr);

        renderTemplate(exchange, "addTask.html", allInOne);
    }

    private void deletionHandler(HttpExchange exchange) {
        int id = getId(exchange);
        JsonSerializer.deleteTask(id);
        redirect303(exchange, "/calendar");
    }

    private void freemarkerCalendarHandler(HttpExchange exchange) {
        Map<String, List<Day>> allDays = new HashMap<>();
        allDays.put("days", JsonSerializer.getDays());
        renderTemplate(exchange, "calendar.html", allDays);
    }

    private void freemarkerDayHandler(HttpExchange exchange) {
        int id = getId(exchange);

        List<Task> tasks = JsonSerializer.getTasksOfDay(id);

        Map<String, List<Task>> allInOne = new HashMap<>();
        allInOne.put("tasks", tasks);

        List<Task> idDay = new ArrayList<>();
        idDay.add(new Task(0, "none", 0, "none", id));
        allInOne.put("idDay", idDay);

        assert tasks != null;
        renderTemplate(exchange, "day.html", allInOne);
    }

    protected int getId(HttpExchange exchange) {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) {
            return -1;
        } else {
            String[] parsed = query.split("=");
            return (Integer.parseInt(parsed[1]));
        }
    }
}


