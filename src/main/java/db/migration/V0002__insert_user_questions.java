package db.migration;

import com.github.f4b6a3.uuid.UuidCreator;
import lombok.extern.log4j.Log4j2;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.Statement;
import java.util.List;

@Log4j2
public class V0002__insert_user_questions extends BaseJavaMigration {

    private final static List<String> userQuestions = List.of(
            "Девичья фамилия матери",
            "Девичья фамилия бабушки",
            "Имя и отчество бабушки",
            "Имя и отчество дедушки",
            "Имя лучшего друга детства",
            "В каком городе познакомились родители",
            "Номер начальной школы",
            "Фамилия классного руководителя в начальной школе",
            "Имя первого домашнего питомца",
            "Модель первой машины",
            "Название любимой спортивной команды",
            "Название любимой книги",
            "Название любимого фильма",
            "Имя и фамилия любимого актёра",
            "Любимое блюдо"
    );

    @Override
    public void migrate(Context context) throws Exception {
        try (Statement insert = context.getConnection().createStatement()) {
            for (String uq : userQuestions) {
                insert.executeUpdate("insert into user_question values ('" + UuidCreator.getTimeOrdered() + "', '" + uq + "')");
            }
        } catch (Exception ex) {
            log.error("[migration:{}]: Ошибка при добавлении секретных вопросов", getClass().getSimpleName(), ex);
            throw ex;
        }
    }
}