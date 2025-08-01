package com.beyond.meongnyang.common.loader;

import com.beyond.meongnyang.species.entity.Size;
import com.beyond.meongnyang.species.entity.Species;
import com.beyond.meongnyang.species.repository.SpeciesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;


// postman test용도
@Component
@RequiredArgsConstructor
public class SpeciesDataLoader implements CommandLineRunner {
    private final SpeciesRepository speciesRepository;

    @Override
    public void run(String... args) throws Exception {
// 🐶 강아지
        speciesRepository.saveAll(List.of(
                // 🐶 강아지
                Species.builder().petOrder("강아지").species("토이푸들").size(Size.SMALL).build(),
                Species.builder().petOrder("강아지").species("말티즈").size(Size.SMALL).build(),
                Species.builder().petOrder("강아지").species("진돗개").size(Size.MEDIUM).build(),
                Species.builder().petOrder("강아지").species("시바견").size(Size.MEDIUM).build(),
                Species.builder().petOrder("강아지").species("골든리트리버").size(Size.LARGE).build(),
                Species.builder().petOrder("강아지").species("포메라니안").size(Size.SMALL).build(),
                Species.builder().petOrder("강아지").species("치와와").size(Size.SMALL).build(),
                Species.builder().petOrder("강아지").species("비숑프리제").size(Size.SMALL).build(),
                Species.builder().petOrder("강아지").species("웰시코기").size(Size.MEDIUM).build(),
                Species.builder().petOrder("강아지").species("도베르만").size(Size.LARGE).build(),

                // 🐱 고양이
                Species.builder().petOrder("고양이").species("러시안블루").size(Size.MEDIUM).build(),
                Species.builder().petOrder("고양이").species("스코티시폴드").size(Size.SMALL).build(),
                Species.builder().petOrder("고양이").species("샴").size(Size.SMALL).build(),
                Species.builder().petOrder("고양이").species("먼치킨").size(Size.SMALL).build(),
                Species.builder().petOrder("고양이").species("브리티시숏헤어").size(Size.MEDIUM).build(),
                Species.builder().petOrder("고양이").species("노르웨이숲고양이").size(Size.LARGE).build(),
                Species.builder().petOrder("고양이").species("메인쿤").size(Size.LARGE).build(),
                Species.builder().petOrder("고양이").species("벵갈").size(Size.MEDIUM).build(),
                Species.builder().petOrder("고양이").species("페르시안").size(Size.MEDIUM).build(),
                Species.builder().petOrder("고양이").species("아비시니안").size(Size.SMALL).build()
        ));
    }
}
