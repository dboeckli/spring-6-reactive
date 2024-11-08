package guru.springframework.spring6reactive.mapper;

import guru.springframework.spring6reactive.dto.BeerDto;
import guru.springframework.spring6reactive.model.Beer;
import org.mapstruct.Mapper;

@Mapper
public interface BeerMapper {
    Beer beerDtoToBeer(BeerDto beerDto);

    BeerDto beerToBeerDto(Beer beer);
}
