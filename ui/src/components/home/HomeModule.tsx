import React from 'react';
import { Container, Row, Col, Image } from 'react-bootstrap';
import scala from 'images/scala.jpg';
import { HomeCarousel, HomeCarouselProps } from 'components/home/HomeCarousel';
import { FaBeer, FaCode } from 'react-icons/fa';
import { IconContext } from 'react-icons';

type BaseSection = {
  body: string;
  title: string;
  image: string;
};

export type HomeProps = {
  base: BaseSection;
  carousel: HomeCarouselProps;
};

class Home extends React.Component<HomeProps> {
  render() {
    const { base, carousel } = this.props;

    return (
      <Container className="body-container">
        <Col xs={8}>
          <Row className="title">
            <h2>{base.title}</h2>
          </Row>
          <Row className="row-content">
            <Col xs={7}>{base.body}</Col>
            <Col className="image">
              <Image src={base.image || scala} fluid />
            </Col>
          </Row>
          <Row className="row-content">
            <Col>
              <HomeCarousel images={carousel.images} />
            </Col>
            <Col className="link-section ">
              <Row>
                <Row>
                  <h2>Where to find me</h2>
                </Row>
                <Col xs={1}>
                  <IconContext.Provider value={{ size: '2em' }}>
                    <FaCode />
                  </IconContext.Provider>
                </Col>
                <Col>
                  <h4>www.linkedin.com</h4>
                </Col>
              </Row>
              <Row>
                <Col xs={1}>
                  <IconContext.Provider value={{ size: '2em' }}>
                    <FaBeer />
                  </IconContext.Provider>
                </Col>
                <Col>
                  <h4>www.github.com</h4>
                </Col>
              </Row>
            </Col>
          </Row>
        </Col>
      </Container>
    );
  }
}

export const HomeModule = Home;
