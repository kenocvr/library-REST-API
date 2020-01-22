import { element, by, ElementFinder } from 'protractor';

export default class BorrowedBookUpdatePage {
  pageTitle: ElementFinder = element(by.id('libraryApp.borrowedBook.home.createOrEditLabel'));
  saveButton: ElementFinder = element(by.id('save-entity'));
  cancelButton: ElementFinder = element(by.id('cancel-save'));
  borrowDateInput: ElementFinder = element(by.css('input#borrowed-book-borrowDate'));
  bookSelect: ElementFinder = element(by.css('select#borrowed-book-book'));
  clientSelect: ElementFinder = element(by.css('select#borrowed-book-client'));

  getPageTitle() {
    return this.pageTitle;
  }

  async setBorrowDateInput(borrowDate) {
    await this.borrowDateInput.sendKeys(borrowDate);
  }

  async getBorrowDateInput() {
    return this.borrowDateInput.getAttribute('value');
  }

  async bookSelectLastOption() {
    await this.bookSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async bookSelectOption(option) {
    await this.bookSelect.sendKeys(option);
  }

  getBookSelect() {
    return this.bookSelect;
  }

  async getBookSelectedOption() {
    return this.bookSelect.element(by.css('option:checked')).getText();
  }

  async clientSelectLastOption() {
    await this.clientSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async clientSelectOption(option) {
    await this.clientSelect.sendKeys(option);
  }

  getClientSelect() {
    return this.clientSelect;
  }

  async getClientSelectedOption() {
    return this.clientSelect.element(by.css('option:checked')).getText();
  }

  async save() {
    await this.saveButton.click();
  }

  async cancel() {
    await this.cancelButton.click();
  }

  getSaveButton() {
    return this.saveButton;
  }
}
