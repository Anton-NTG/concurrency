package course.concurrency.m2_async.executors.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SpringBootAsyncTest {

    @Autowired
    private AsyncClassTest testClass;

    // this method executes after application start
    @EventListener(ApplicationReadyEvent.class)
    public void actionAfterStartup() {
        testClass.runAsyncTask();
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBootAsyncTest.class, args);
    }
}


/*

1. Уже стоит аннотация @Async
2. Добавил AsyncClassTest.internalTask аннотацию @Async
3. Запустил, выполнились в main потоке.
 Добавил @EnableAsync перед public class SpringBootAsyncTest {
 получилось так:
runAsyncTask: task-1
internalTask: task-1
4. @Async запускает метод, перед которым она стоит в отдельном потоке (при условии что есть  @EnableAsync)

5.
Не получается:(.

Вероятно этот код неспроста
    @Autowired
    @Qualifier("applicationTaskExecutor")
    private ThreadPoolTaskExecutor executor;

    и его надо как-то заюзать, но не придумал как, пытаюсь воспользоваться подходами отсюда
    https://www.linkedin.com/pulse/asynchronous-calls-spring-boot-using-async-annotation-omar-ismail
    но даже используя 2 разных эксекьютора

        @Async("threadPoolTaskExecutor1")
    public void runAsyncTask() {
        System.out.println("runAsyncTask: " + Thread.currentThread().getName());
        internalTask();
    }
    @Async("threadPoolTaskExecutor2")
    public void internalTask() {
        System.out.println("internalTask: " + Thread.currentThread().getName());
    }

    Результат

    runAsyncTask: CustomExecutor1::1
    internalTask: CustomExecutor1::1

    То есть они вроде как на кастомном эксекьюторе выполняются, но на одном!

    Как тут надо поступить?

    6. applicationTaskExecutor

    Я нашел такую инфу

    applicationTaskExecutor зависит от ThreadPoolTaskExecutor по-умолчанию, а у того в свою очередь:

    Core pool size: 1
    Maximum pool size: Integer.MAX_VALUE
    Thread keep-alive time: 60 seconds
    Queue capacity: Integer.MAX_VALUE
    Rejection policy: AbortPolicy

    7. Эксекьютор по-умолчанию переопределяем когда нужны кастомные параметры, либо специфическое поведение под
    нагрузки и задачи.
 */