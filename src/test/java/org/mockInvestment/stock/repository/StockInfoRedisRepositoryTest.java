package org.mockInvestment.stock.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockInvestment.stock.domain.RecentStockInfo;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StockInfoRedisRepositoryTest {

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @InjectMocks
    private RecentStockInfoRedisRepository recentStockInfoRedisRepository;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("코드로 유효한 문자열을 얻은 뒤, RecentStockInfo 로 역직렬화한다.")
    void findByStockCode_success_validString() {
        String validString = "{\n" +
                "  \"symbol\": \"AAPL\",\n" +
                "  \"name\": \"Apple\",\n" +
                "  \"base\": 173.72,\n" +
                "  \"close\": 175.765,\n" +
                "  \"curr\": 175.765,\n" +
                "  \"high\": 175.85,\n" +
                "  \"low\": 173.03,\n" +
                "  \"open\": 174.09,\n" +
                "  \"volume\": 26891419\n" +
                "}";

        when(redisTemplate.opsForValue().get(anyString()))
                .thenReturn(validString);

        RecentStockInfo recentStockInfo = recentStockInfoRedisRepository.findByStockCode("CODE")
                .orElseThrow();

        assertThat(recentStockInfo.name()).isEqualTo("Apple");
    }

    @Test
    @DisplayName("코드로 빈 문자열을 얻으면, Optional.empty() 를 반환한다.")
    void findByStockCode_empty_emptyString() {
        when(redisTemplate.opsForValue().get(anyString()))
                .thenReturn(null);

        assertThat(recentStockInfoRedisRepository.findByStockCode("CODE"))
                .isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("코드로 문자열을 얻은뒤, RecentStockInfo 로 역직렬화하는데 실패하면, Optional.empty() 를 반환한다.")
    void findByStockCode_empty_serialize() {
        String invalidString = "{\n" +
                "  \"XXX\": \"XX\",\n" +
                "  \"YYY\": \"YY\"\n" +
                "}";
        when(redisTemplate.opsForValue().get(anyString()))
                .thenReturn(invalidString);

        assertThat(recentStockInfoRedisRepository.findByStockCode("CODE"))
                .isEqualTo(Optional.empty());
    }

}