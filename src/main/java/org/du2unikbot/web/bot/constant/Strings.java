package org.du2unikbot.web.bot.constant;

public class Strings {
    public static final String ERROR_TRANSITION = "Ошибка при переходе в статус %s";
    public static final String HTML_PARSE_MODE = "HTML";
    public static final String TIME_PATTERN = "HH:mm";
    public static final String THUMBS_UP_EMOJI = ":thumbsup:";
    public static final String CROSS_EMOJI = ":x:";
    public static final String ID_ITEM = "ID: ";
    public static final String START_ITEM = "Старт: ";
    public static final String FINISH_ITEM = "Финиш: ";
    public static final String FREE_SEATS_ITEM = "Свободных мест: ";
    public static final String MEETING_TIME_ITEM = "Время встречи: ";
    public static final String PARTICIPANTS_ITEM = "Участники: ";
    public static final String BOT_GREETING = "Здравствуйте, %s";
    public static final String BOT_DESCRIPTION = "Я бот для совместных поездок на такси от Деревни Универсиады " +
            "до вашего университета и обратно. С моей помощью вы сможете найти себе попутчика со всего общежития " +
            "и сэкономить свои финансы.";
    public static final String BOT_TERMS_TITLE = "ТЕРМИНЫ:";
    public static final String BOT_TERMS = "Тикет - это запрос на совместную поездку на такси. " +
            "Тикет имеет свой уникальный ID, точку старта, точку финиша, количество свободных мест, " +
            "время встречи и непосредственно участников.";
    public static final String BOT_RULES_TITLE = "ПРАВИЛА И РЕКОМЕНДАЦИИ:";
    public static final String BOT_RULES =
            "0. Следуйте указаниям бота, и у вас все получится.\n" +
            "1. Чтобы вступить в тикет, можно либо выбрать его из уже имеющихся " +
            "в списке списке тикетов, либо создать собственный.\n" +
            "2. Как только наступит время встречи или наберется ровно 4 человека, тикет становится неактивным.\n" +
            "3. Разрешено быть участником только одного активного тикета.\n" +
            "4. При создании или выборе существующего тикета, вы можете указать число друзей, которые поедут с вами.\n" +
            "5. Чтобы гарантировать своевременное прибытие к точке старта, " +
                    "время встречи должно быть не менее чем через 10 минут от текущего момента.\n";
    public static final String ALL_TICKETS = "Все тикеты";
    public static final String CREATE_TICKET = "Создать тикет";
    public static final String MY_TICKETS = "Мои тикеты";
    public static final String START_POINT_SELECTION_TITLE = "Укажите точку старта:";
    public static final String FINISH_POINT_SELECTION_TITLE = "Укажите точку финиша:";
    public static final String RESERVED_SEATS_SELECTION_TITLE = "Укажите количество забронированных мест:";
    public static final String MEETING_TIME_SELECTION_TITLE = "Укажите время (ЧЧ:ММ) ответом на это сообщение:";
    public static final String TICKET_CONFIRMATION_TITLE = "Подтвердить введенные данные?";
    public static final String THERE_IS_NO_TICKET = "Пока не создан ни один тикет!";
    public static final String YOU_HAVE_NO_TICKET = "У вас нет тикетов!";
    public static final String TICKET_ENROLLMENT = "Вы уже записаны в тикет с ID: %d!";
    public static final String TICKET_SELECTION_SUCCESS = "Вы успешно выбрали тикет с ID: %d";
    public static final String TICKET_DELETING_SUCCESS = "Ваш тикет с ID: %d успешно удален";
    public static final String TICKET_DELETING_FAILURE = "Пользователь не найден. Невозможно удалить тикет c ID: %d";
    public static final String EXISTING_MEETING_TIME_ERROR = "Тикет на указанное время для вас уже существует!";
    public static final String TOO_EARLY_MEETING_TIME_ERROR = "Время встречи должно быть хотя бы на 10 минут позже текущего!";
    public static final String INVALID_MEETING_TIME_ERROR = "Некорректный формат времени. Введите время в формате ЧЧ:ММ (например, 14:30)";
    public static final String TICKET_CONFIRMATION_SUCCESS = "Успешно создан тикет с ID: %d";
    public static final String TICKET_CONFIRMATION_CANCELLATION = "Ваш тикет не создан!";
    public static final String ADDING_FRIENDS_TITLE = "Укажите сколько друзей поедет с вами:";
    public static final String ERROR_ONLY_ONE_ACTIVE_TICKET_ALLOWED = "Вы можете иметь только один активный тикет одновременно!";
    public static final String NOT_EXISTING_TICKET_ERROR = "Данного тикета не существует! Обновите список тикетов";
    public static final String START_POINT_CANCELLATION = "Выбор точки старта отменен";
    public static final String FINISH_POINT_CANCELLATION = "Выбор точки финиша отменен";
    public static final String RESERVED_SEATS_CANCELLATION = "Выбор зарезервированных мест отменен";
    public static final String MEETING_TIME_SELECTION_CANCELLATION = "Выбор времени встречи отменен";
    public static final String ADDING_FRIENDS_CANCELLATION = "Выбор тикета отменен";
}
