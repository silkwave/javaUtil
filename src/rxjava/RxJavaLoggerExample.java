package rxjava;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class RxJavaLoggerExample {

    private static final Logger logger = Logger.getLogger(RxJavaLoggerExample.class.getName());

    public static void main(String[] args) throws IOException {
        // Set up file logging
        FileHandler fileHandler = new FileHandler("rxjava.log");
        SimpleFormatter formatter = new SimpleFormatter();
        fileHandler.setFormatter(formatter);
        logger.addHandler(fileHandler);

        Observable<String> observable = Observable.just("Hello", "World!");

        observable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                        logger.info("Subscribed");
                    }

                    @Override
                    public void onNext(String s) {
                        logger.info("Received: " + s);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        logger.info("Error: " + throwable.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        logger.info("Completed");
                    }
                });

        // Ensure proper shutdown of the file handler
        fileHandler.close();
    }
}
