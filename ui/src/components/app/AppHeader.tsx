import companyLogo from 'images/logo.png';
import React from 'react';
import { Button, Col, Container, Form, FormControl, Image, Nav, Navbar, NavDropdown } from 'react-bootstrap';
import { Link } from 'react-router-dom';

const Header: React.FC = ({}) => (
  <Navbar className="app-header">
    <Container>
      <Col xs={2}>
        <Navbar.Brand>
          <Image src={companyLogo} className="app-logo" />
        </Navbar.Brand>
      </Col>
      <Col xs={7}>
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="me-auto">
            <Link className="app-header-nav nav-link" role="button" to="/">
              Home
            </Link>
            <Link className="app-header-nav nav-link" role="button" to="/test">
              Link
            </Link>
            <NavDropdown className="app-header-nav" title="Dropdown" id="basic-nav-dropdown">
              <Link className="dropdown-item" role="button" to="/test">
                Link
              </Link>
              <Link className="dropdown-item" role="button" to="/test">
                Link2
              </Link>
              <Link className="dropdown-item" role="button" to="/test">
                Link3
              </Link>

              <NavDropdown.Divider />
              <NavDropdown.Item>Separated link</NavDropdown.Item>
            </NavDropdown>
          </Nav>
        </Navbar.Collapse>
      </Col>
      <Col>
        <Form className="d-flex">
          <FormControl type="search" placeholder="Search" className="me-2" aria-label="Search" />
          <Button>Search</Button>
        </Form>
      </Col>
    </Container>
  </Navbar>
);

export const AppHeader = Header;
