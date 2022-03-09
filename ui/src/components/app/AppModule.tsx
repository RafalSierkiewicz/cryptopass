import { HomeModule } from 'components';
import { AppHeader } from 'components/app/AppHeader';
import logo from 'images/logo.svg';
import React from 'react';
import { Col, Container, Row } from 'react-bootstrap';
import { Route, Routes } from 'react-router';

class AppRouter extends React.Component {
  render() {
    return (
      <Container fluid>
        <AppHeader />
        <Routes>
          <Route path="/" element={<HomeModule />} />
        </Routes>
      </Container>
    );
  }
}

export const AppModule = AppRouter;
