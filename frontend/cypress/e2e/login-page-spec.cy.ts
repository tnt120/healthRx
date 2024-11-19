describe('Logowanie użytkownika', () => {
  it('powinno zalogować użytkownika i przekierować na dashboard użytkownika', () => {
    cy.visit('/login');

    cy.get('input[formControlName="email"]').type('user@gmail.com');
    cy.get('input[formControlName="password"]').type('user');

    cy.get('button.submit-button').click();

    cy.url().should('eq', 'http://localhost:4200/user/parameters');

    cy.contains('Parametry do uzupełnienia').should('be.visible');

    cy.contains('Dzisiejsze parametry').should('be.visible');
  });
});

describe('Logowanie lekarza', () => {
  it('powinno zalogować lekarza i przekierować na dashboard lekarza', () => {
    cy.visit('/login');

    cy.get('input[formControlName="email"]').type('lekarz1@gmail.com');
    cy.get('input[formControlName="password"]').type('lekerz');

    cy.get('button.submit-button').click();

    cy.url().should('eq', 'http://localhost:4200/doctor/patients');

    cy.contains('Moi pacjenci').should('be.visible');
  });
});
