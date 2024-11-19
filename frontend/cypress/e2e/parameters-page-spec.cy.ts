describe('Statystyki użytkownika', () => {
  beforeEach(() => {
    cy.visit('/login');

    cy.get('input[formControlName="email"]').type('user@gmail.com');
    cy.get('input[formControlName="password"]').type('user');

    cy.get('button.submit-button').click();

    cy.url().should('eq', 'http://localhost:4200/user/parameters');

    cy.contains('Parametry').click();
  });
  it('Przejście na stronę', () => {
    cy.contains('p', 'Wybór parametrów')
      .click();

      cy.url().should('eq', 'http://localhost:4200/user/settings?parametersChange=true');

      cy.contains('h4', 'Wybrane parametry').should('be.visible');
  });
});
