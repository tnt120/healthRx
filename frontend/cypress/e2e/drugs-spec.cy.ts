describe('Apteczka leków', () => {
  beforeEach(() => {
    cy.visit('/login');

    cy.get('input[formControlName="email"]').type('user@gmail.com');
    cy.get('input[formControlName="password"]').type('user');

    cy.get('button.submit-button').click();

    cy.url().should('eq', 'http://localhost:4200/user/parameters');

    cy.contains('Apteczka').click();
  });
  it('Przejście na stronę', () => {
    cy.url().should('eq', 'http://localhost:4200/user/cabinet');

    cy.contains('h3', 'Leki na dziś')
      .parent()
      .parent()
      .find('.collapse-icon')
      .click();

    cy.contains('p.subtitle-3', 'Do zażycia');
    cy.contains('p.subtitle-3', 'Zażyte')
  });
  it('Dodanie leku', () => {
    cy.url().should('eq', 'http://localhost:4200/user/cabinet');

    cy.get('.top-button-container').contains('Dodaj lek').click();

    cy.url().should('eq', 'http://localhost:4200/user/cabinet/add');
    cy.contains('Wybór leku').should('be.visible');

    cy.get('div.main-container')
      .children()
      .children()
      .children()
      .contains('Wybierz')
      .click();

    cy.get('mat-form-field').first().click();
    cy.get('mat-option').first().click();

    cy.contains('Dawkowanie').should('be.visible');

    cy.get('.grid').children().first().children().children().first().click();

    cy.get('mat-form-field').contains('Dawka').type('1');


    cy.get('div.dose-time-container')
      .children()
      .children()
      .contains('Godzina')
      .click();

    cy.get('mat-option').contains('18').click();

    cy.get('div.dose-time-container')
      .children()
      .children()
      .contains('Minuta')
      .click();

    cy.get('mat-option').contains('15').click();

    cy.get('input[placeholder="Od"]').type('10.10.2024', { force: true });

    cy.contains('Zapisz').click();

    cy.url().should('eq', 'http://localhost:4200/user/cabinet');

    cy.contains('Sukces');
  })
});
