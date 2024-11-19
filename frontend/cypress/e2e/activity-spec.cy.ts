describe('Aktywności użytkownika', () => {
  beforeEach(() => {
    cy.visit('/login');

    cy.get('input[formControlName="email"]').type('user@gmail.com');
    cy.get('input[formControlName="password"]').type('user');

    cy.get('button.submit-button').click();

    cy.url().should('eq', 'http://localhost:4200/user/parameters');

    cy.contains('Aktywność').click();
  });
  it('Przejście na stronę', () => {
    cy.url().should('eq', 'http://localhost:4200/user/activities');

    cy.get('app-header-bar').contains('Aktywności fizyczne').should('be.visible');

    cy.contains('h3', 'Twoje dzisiejsze aktywności')
      .parent()
      .parent()
      .find('.collapse-icon')
      .click();

    cy.contains('h3', 'Twoje wszystkie aktywności')
      .parent()
      .parent()
      .find('.collapse-icon')
      .click();
  });
  it('Dodanie aktywności', () => {
    cy.url().should('eq', 'http://localhost:4200/user/activities');

    cy.get('.top-button-container').contains('Dodaj aktywność').click();

    cy.get('mat-dialog-container').should('be.visible');

    cy.get('mat-dialog-container').get('mat-form-field').first().click();
    cy.get('mat-option').first().click();

    cy.get('mat-dialog-container').get('mat-form-field').eq(1).type('10.10.2024');

    cy.get('mat-dialog-container').get('mat-form-field').eq(2).click()
    cy.get('mat-option').contains('20').click();

    cy.get('mat-dialog-container').get('mat-form-field').eq(3).click()
    cy.get('mat-option').contains('15').click();

    cy.get('mat-dialog-container').get('mat-form-field').eq(4).type('50');

    cy.get('mat-dialog-container').get('button').contains('Zapisz').click();

    cy.contains('Sukces');
  });
});
