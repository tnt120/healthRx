describe('Statystyki użytkownika', () => {
  beforeEach(() => {
    cy.visit('/login');

    cy.get('input[formControlName="email"]').type('user@gmail.com');
    cy.get('input[formControlName="password"]').type('user');

    cy.get('button.submit-button').click();

    cy.url().should('eq', 'http://localhost:4200/user/parameters');

    cy.contains('Statystyki').click();
  });
  it('Utworzenie wykresu statystyk parametrów użytkownika', () => {
    cy.contains('h3', 'Parametry').should('be.visible');

    cy.contains('h3', 'Parametry')
      .parent()
      .parent()
      .find('.collapse-icon')
      .click();

      cy.get('div.buttons button[mat-icon-button]')
      .should('exist')
      .click();

      cy.get('div.parameters-chart-filter-container').contains('Parametr', { timeout: 10000 })
      .children()
      .should('exist')
      .click();

      cy.get('mat-option')
      .first()
      .click();

      cy.get('div.parameters-chart-filter-container').contains('Zakres', { timeout: 10000 })
      .children()
      .should('exist')
      .click();

      cy.get('mat-option')
      .eq(7)
      .click();
  });

  it('Utworzenie wykresu statystyk leków użytkownika', () => {
    cy.contains('h3', 'Leki').should('be.visible');

    cy.contains('h3', 'Leki')
      .parent()
      .parent()
      .find('.collapse-icon')
      .click();

      cy.get('div.buttons button[mat-icon-button]')
      .should('exist')
      .click();

      cy.get('div.drugs-chart-filter-container').contains('Lek', { timeout: 10000 })
      .children()
      .should('exist')
      .click();

      cy.get('mat-option')
      .first()
      .click();

      cy.get('div.drugs-chart-filter-container').contains('Zakres', { timeout: 10000 })
      .children()
      .should('exist')
      .click();

      cy.get('mat-option')
      .eq(7)
      .click();
  });

  it('Utworzenie wykresu statystyk aktywności użytkownika', () => {
    cy.contains('h3', 'Parametry').should('be.visible');

    cy.contains('h3', 'Parametry')
      .parent()
      .parent()
      .find('.collapse-icon')
      .click();

      cy.get('div.buttons button[mat-icon-button]')
      .should('exist')
      .click();

      cy.get('div.parameters-chart-filter-container').contains('Parametr', { timeout: 10000 })
      .children()
      .should('exist')
      .click();

      cy.get('mat-option')
      .first()
      .click();

      cy.get('div.parameters-chart-filter-container').contains('Zakres', { timeout: 10000 })
      .children()
      .should('exist')
      .click();

      cy.get('mat-option')
      .eq(7)
      .click();
  });
});
