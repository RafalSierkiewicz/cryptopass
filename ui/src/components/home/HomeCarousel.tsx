import React, { useState } from 'react';
import { Carousel } from 'react-bootstrap';

type Image = {
  url: string;
  alt?: string;
};

export type HomeCarouselProps = {
  images: Image[];
};
const ControlledCarousel: React.FC<HomeCarouselProps> = ({ images }) => {
  const [index, setIndex] = useState(0);

  const handleSelect = (selectedIndex, e) => {
    setIndex(selectedIndex);
  };

  return (
    <Carousel activeIndex={index} onSelect={handleSelect} variant="dark">
      {images.map((img, idx) => (
        <Carousel.Item>
          <img className="d-block w-100" src={img.url} alt={img.alt || `carousel${idx}`} />
        </Carousel.Item>
      ))}
    </Carousel>
  );
};

export const HomeCarousel = ControlledCarousel;
