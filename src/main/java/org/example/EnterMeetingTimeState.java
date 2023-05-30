package org.example;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class EnterMeetingTimeState extends State {
    public EnterMeetingTimeState(Du2UnikBot du2UnikBot) {
        super(du2UnikBot);
    }
    @Override
    public void handleMessage(Message message) {
        // Инициализируем переменные
        super.handleMessage(message);

        Message replyMessage = message.getReplyToMessage();
        String replyMessageText = replyMessage.getText();
        if (replyMessageText.equals("Укажите время (ЧЧ:ММ):")) {
            handleMeetingTime(messageText, chatId, message);
        }
//        } else if (replyMessageText.equals("Для участия в нашей программе, пожалуйста, создайте группу и предоставьте мне ссылку на нее.")){
//
//            String link = "https://t.me/joinchat/...";
//            String regex = "^https://t.me/joinchat/[\\w\\-]+$";
//            boolean isValid = link.matches(regex);
//
//        }
    }
    private void handleMeetingTime(String replyMessageText, long chatId, Message message) {
        StringBuilder errorMessage = new StringBuilder();
        if (isValidMeetingTime(replyMessageText, errorMessage)) {
            // Допустимое время встречи
            du2UnikBot.setCurrentState("confirm_ticket_creation");

            // Сохранение времени встречи в ticket
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            du2UnikBot.getCurrentTicket().setMeetingTime(LocalTime.parse(replyMessageText, formatter));

            // Получение сообщения, на которое был дан ответ
            Message replyMessage = message.getReplyToMessage();
            int messageRequestId = replyMessage.getMessageId();

            // Удаление ответа
            du2UnikBot.deleteMessage(chatId, message.getMessageId());

            // Отправка копии запроса
            du2UnikBot.editMessageText(chatId, messageRequestId, replyMessage.getText() + " " + replyMessageText);

            // Удаление оригинала запроса
            du2UnikBot.deleteMessage(chatId, messageRequestId);

            // Отправка следующего сообщения формы
            SendMessage request = du2UnikBot.getTicketCreationForm().getFormMessages().get("confirmation");
            request.setChatId(chatId);
            try {
                du2UnikBot.execute(request);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        } else {
            // Недопустимое время встречи
            du2UnikBot.sendMessage(chatId, errorMessage.toString());
            SendMessage request = du2UnikBot.getTicketCreationForm().getFormMessages().get("meeting_time");
            request.setChatId(chatId);
            try {
                du2UnikBot.execute(request);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private boolean isValidMeetingTime(String meetingTime, StringBuilder errorMessage) {
        try {
            // Проверка формата времени
            LocalTime inputTime = LocalTime.parse(meetingTime);
            LocalTime currentTime = LocalTime.now();

            // Проход по значениям тикетов и проверка времени и пользователя
            for (Ticket currentTicket : du2UnikBot.getTickets().values()) {
                if (currentTicket.getMeetingTime().equals(LocalTime.parse(meetingTime))
                        && currentTicket.getUsernames().contains(du2UnikBot.getCurrentTicket().getUsernames().get(0))) {
                    errorMessage.append("Тикет на указанное время для вас уже существует!");
                    return false;
                }
            }

            // Проверка, что введенное время на 10 минут больше текущего времени
            if (inputTime.isBefore(currentTime.plusMinutes(10))) {
                errorMessage.append("Время встречи должно быть не менее чем через 10 минут от текущего времени");
                return false;
            }

            return true; // Время встречи валидно
        } catch (DateTimeParseException e) {
            errorMessage.append("Некорректный формат времени. Введите время в формате ЧЧ:ММ (например, 14:30)");
            return false;
        }
    }
}
