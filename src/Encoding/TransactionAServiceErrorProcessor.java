package Encoding;
// package util;

// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
// import java.util.concurrent.CompletableFuture;

// // 스프링부트 자바 오라클DB @Transactional 
// // 금융결제원 전자금융공동망 개설(타발) 예금 입출금 거래 
// // TransactionAService.java
// // 금융 거래 처리를 위한 서비스 클래스
// @Service
// public class TransactionAService {

//     private final TransactionRepository transactionRepository;
//     private final AccountingService accountingService;
//     private final LogRepository logRepository;
//     private final FepService fepService;
//     private final TransactionAServiceErrorProcessor errorProcessor;

//     // 생성자를 통한 의존성 주입
//     public TransactionAService(
//         TransactionRepository transactionRepository,
//         AccountingService accountingService,
//         LogRepository logRepository,
//         FepService fepService,
//         TransactionAServiceErrorProcessor errorProcessor
//     ) {
//         this.transactionRepository = transactionRepository;
//         this.accountingService = accountingService;
//         this.logRepository = logRepository;
//         this.fepService = fepService;
//         this.errorProcessor = errorProcessor;
//     }

//     /**
//      * 금융 거래를 처리하는 메서드
//      * @param request 거래 요청 객체
//      */
//     @Transactional
//     public void processTransaction(TransactionRequest request) {
//         try {
//             // 거래 유효성 검사
//             validateTransaction(request);

//             // 거래 요청을 엔티티로 변환 후 저장
//             transactionRepository.save(convertToEntity(request));

//             // 회계 처리
//             accountingService.processAccounting(request);

//             // 거래 로그 저장
//             logRepository.save(createLogEntry(request));

//             // FEP 메시지 전송
//             fepService.sendFEPMessage(request);
//         } catch (Exception e) {
//             // 예외 발생 시 에러 처리
//             handleTransactionError(e, request);
//             throw e;  // 트랜잭션 롤백 발생
//         }
//     }

//     /**
//      * 에러 발생 시 호출되는 메서드
//      * @param e 발생한 예외
//      * @param request 거래 요청 객체
//      */
//     private void handleTransactionError(Exception e, TransactionRequest request) {
//         // CompletableFuture를 이용한 비동기 처리
//         CompletableFuture.runAsync(() -> {
//             // processError 호출
//             errorProcessor.processError(e, request);
//         });
//     }
// }


// package util;

// import org.springframework.stereotype.Service;

// // 에러 처리를 담당하는 서비스 클래스
// @Service
// public class TransactionAServiceErrorProcessor {

//     private final LogRepository logRepository;
//     private final FepService fepService;

//     // 생성자를 통한 의존성 주입
//     public TransactionAServiceErrorProcessor(LogRepository logRepository, FepService fepService) {
//         this.logRepository = logRepository;
//         this.fepService = fepService;
//     }

//     /**
//      * 에러를 처리하는 메서드
//      * @param e 발생한 예외
//      * @param request 거래 요청 객체
//      */
//     public void processError(Exception e, TransactionRequest request) {
//         // 오류 로그 저장
//         logRepository.save(createErrorLogEntry(request, e));

//         // 오류 정보를 FEP로 전송
//         fepService.sendErrorFEPMessage(e, request);
//     }
// }
