package org.runaway.utils;

public class TelegramBot /*extends TelegramLongPollingBot*/ {
/*
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chat_id = update.getMessage().getChatId();
            if (update.getMessage().getText().equalsIgnoreCase("/donate")) {
                String today = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                if (EConfig.DONATE.getConfig().get("donate-log." + today) != null) {
                    StringBuilder sb = new StringBuilder();
                    List<String> s = EConfig.DONATE.getConfig().getStringList("donate-log." + today);
                    int i = 0;
                    for (String string : s) {
                        sb.append(i++ + 1).append(". ").append(string).append("\n");
                    }
                    sb.append("- \nСтатистика на ").append(today);
                    SendMessage message = new SendMessage() // Create a message object object
                            .setChatId(chat_id)
                            .setText(sb.toString());
                    try {
                        execute(message);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    SendMessage message = new SendMessage() // Create a message object object
                            .setChatId(chat_id)
                            .setText("Сегодня ещё не было донатов(");
                    try {
                        execute(message);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public String getBotUsername() {
        return Prison.getInstance().bot_username;
    }

    @Override
    public String getBotToken() {
        return Prison.getInstance().bot_token;
    }*/
}
