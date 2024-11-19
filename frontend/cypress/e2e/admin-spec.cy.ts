describe('Strona administratora', () => {
  beforeEach(() => {
    cy.visit('/login');

    cy.get('input[formControlName="email"]').type('admin1@gmail.com');
    cy.get('input[formControlName="password"]').type('admin');

    cy.get('button.submit-button').click();

    cy.url().should('eq', 'http://localhost:4200/admin/dashboard');
  });
  it('Strona dashboardu', () => {
    cy.get('app-header-bar').contains('Dashboard').should('be.visible');
    cy.get('app-dashboard-card').first().contains('Liczba użytkowników').should('be.visible');
    cy.get('app-dashboard-card').eq(1).contains('Liczba lekarzy').should('be.visible');
    cy.get('app-dashboard-card').eq(2).contains('Liczba wniosków').should('be.visible');
    cy.get('app-dashboard-card').eq(3).contains('Liczba leków').should('be.visible');
    cy.get('app-dashboard-card').eq(4).contains('Liczba parametrów').should('be.visible');
    cy.get('app-dashboard-card').last().contains('Liczba aktywności').should('be.visible');
  });
  it('Strona wniosków', () => {
    cy.contains('Wnioski').click();

    cy.url().should('eq', 'http://localhost:4200/admin/approvals');

    cy.get('app-header-bar').contains('Weryfikacja lekarzy').should('be.visible');
  });
  it('Strona parametrów', () => {
    cy.contains('Parametry').click();

    cy.url().should('eq', 'http://localhost:4200/admin/parameters-manage');

    cy.get('app-header-bar').contains('Zarządzanie parametrami').should('be.visible');

    cy.contains('Dodaj nowy parametr').should('be.visible');

    cy.contains('Lista parametrów').should('be.visible');
  });
  it('Strona aktywności', () => {
    cy.contains('Aktywności').click();

    cy.url().should('eq', 'http://localhost:4200/admin/activities-manage');

    cy.get('app-header-bar').contains('Zarządzanie aktywnościami').should('be.visible');

    cy.contains('Dodaj nową aktywność').should('be.visible');

    cy.contains('Lista aktywności').should('be.visible');
  });
  it('Strona aktywności', () => {
    cy.contains('Użytkownicy').click();

    cy.url().should('eq', 'http://localhost:4200/admin/users-manage');

    cy.get('app-header-bar').contains('Zarządzanie użytkownikami').should('be.visible');

    cy.contains('Szukaj').should('be.visible');
  });
  it('Dodanie parametru', () => {
    cy.contains('Parametry').click();

    cy.url().should('eq', 'http://localhost:4200/admin/parameters-manage');

    cy.get('button').contains('Dodaj nowy parametr').click();

    cy.get('mat-dialog-container').should('be.visible');

    cy.get('mat-dialog-container').get('mat-form-field').first().type('Testowy parametr');

    cy.get('mat-dialog-container').get('mat-form-field').eq(1).click();
    cy.get('mat-option').first().click();

    cy.get('mat-dialog-container').get('mat-form-field').eq(2).type('Testowa wskazówka');

    cy.get('mat-dialog-container').get('mat-form-field').eq(3).type('5');

    cy.get('mat-dialog-container').get('mat-form-field').eq(4).type('10');

    cy.get('mat-dialog-container').get('mat-form-field').eq(5).type('1');

    cy.get('mat-dialog-container').get('mat-form-field').last().type('50');

    cy.get('mat-dialog-actions').get('button').contains('Zapisz').should('not.be.disabled');
  });
  it('Dodanie aktywności', () => {
    cy.contains('Aktywności').click();

    cy.url().should('eq', 'http://localhost:4200/admin/activities-manage');

    cy.get('button').contains('Dodaj nową aktywność').click();

    cy.get('mat-dialog-container').should('be.visible');

    cy.get('mat-dialog-container').get('mat-form-field').first().type('Testowa aktywność');

    cy.get('mat-dialog-container').get('mat-form-field').last().type('10');

    cy.get('mat-dialog-actions').get('button').contains('Zapisz').should('not.be.disabled');
  });
});
