package trash;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface Handleable {
    void handleUpdate(Update update);
}
