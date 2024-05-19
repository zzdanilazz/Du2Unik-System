package org.du2unikbot.web.bot.constant;

public enum CallbackButton {
    START_DU("start_du", "ДУ"),
    START_DVOYKA("start_dvoyka", "Двойка"),
    START_FF("start_ff", "ФФ"),
    FINISH_DVOYKA("finish_dvoyka", "Двойка"),
    FINISH_FF("finish_ff", "ФФ"),
    SEATS_1("seats_1", "1"),
    SEATS_2("seats_2", "2"),
    SEATS_3("seats_3", "3"),
    CONFIRM_TICKET_CREATION("confirm_ticket_creation", "Да"),
    NOT_CONFIRM_TICKET_CREATION("not_confirm_ticket_creation", "Нет"),
    SELECT("select", "Выбрать"),
    DELETE("delete", "Удалить"),
    FRIENDS_0("friends_0", "0"),
    FRIENDS_1("friends_1", "1"),
    FRIENDS_2("friends_2", "2"),
    ;

    private final String option;
    private final String callback;

    CallbackButton(String callback, String option) {
        this.callback = callback;
        this.option = option;
    }

    public String getOption() {
        return option;
    }

    public String getCallback() {
        return callback;
    }

    public static CallbackButton fromCallback(String callback) {
        for (CallbackButton button : CallbackButton.values()) {
            if (button.getCallback().equals(callback)) {
                return button;
            }
        }
        throw new IllegalArgumentException("No enum constant with callback " + callback);
    }
}
