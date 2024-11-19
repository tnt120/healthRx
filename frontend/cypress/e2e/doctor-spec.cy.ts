describe('Strona lekarza', () => {
  beforeEach(() => {
    cy.visit('/login');

    cy.get('input[formControlName="email"]').type('lekarz1@gmail.com');
    cy.get('input[formControlName="password"]').type('lekarz');

    cy.get('button.submit-button').click();

    cy.url().should('eq', 'http://localhost:4200/doctor/patients');
  });
  it('Strona główna', () => {
    cy.contains('Moi pacjenci').should('be.visible');
  });
  it('Zaproszenia', () => {
    cy.contains('Zaproszenia').click();

    cy.url().should('eq', 'http://localhost:4200/doctor/approvals');

    cy.contains('Zaproszenia').should('be.visible');
  });
  it('Wiadomości', () => {
    cy.contains('Wiadomości').click();

    cy.url().should('eq', 'http://localhost:4200/doctor/messages');

    cy.contains('Wiadomości').should('be.visible');

    cy.get('app-chat-conversations-tab').contains('Wszystkie rozmowy').should('be.visible');
  })
})
