package org.mockInvestment.stockTicker.application;

import lombok.RequiredArgsConstructor;
import org.mockInvestment.auth.dto.AuthInfo;
import org.mockInvestment.member.domain.Member;
import org.mockInvestment.member.exception.MemberNotFoundException;
import org.mockInvestment.member.repository.MemberRepository;
import org.mockInvestment.stockTicker.dto.StockTickerResponse;
import org.mockInvestment.stockTicker.domain.StockTicker;
import org.mockInvestment.stockTicker.dto.StockTickersResponse;
import org.mockInvestment.stockTicker.exception.StockTickerNotFoundException;
import org.mockInvestment.stockTicker.repository.StockTickerLikeRepository;
import org.mockInvestment.stockTicker.repository.StockTickerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockTickerFindService {

    private final StockTickerRepository stockTickerRepository;

    private final StockTickerLikeRepository stockTickerLikeRepository;

    private final MemberRepository memberRepository;


    public StockTickerResponse findStockTickerByCode(String stockCode, AuthInfo authInfo) {
        StockTicker stockTicker = stockTickerRepository.findByCode(stockCode)
                .orElseThrow(StockTickerNotFoundException::new);
        Member member = memberRepository.findById(authInfo.getId())
                .orElseThrow(MemberNotFoundException::new);
        boolean isLiked = stockTickerLikeRepository.existsByStockTickerAndMember(stockTicker.getCode(), member);
        return new StockTickerResponse(stockTicker.getName(), stockTicker.getCode(), isLiked);
    }

    public StockTickersResponse findStockTickersByKeyword(String keyword, AuthInfo authInfo) {
        if (keyword.isEmpty())
            return new StockTickersResponse(new ArrayList<>());
        Member member = memberRepository.findById(authInfo.getId())
                .orElseThrow(MemberNotFoundException::new);
        List<StockTickerResponse> responses = stockTickerRepository.findAllByKeyword(keyword).stream()
                .map(codeAndName -> {
                    boolean isLiked = stockTickerLikeRepository.existsByStockTickerAndMember(codeAndName[0], member);
                    return new StockTickerResponse(codeAndName[1], codeAndName[0], isLiked);
                })
                .toList();
        return new StockTickersResponse(responses);
    }

}
