import { element, by, ElementFinder } from 'protractor';

export default class BookUpdatePage {
  pageTitle: ElementFinder = element(by.id('libraryApp.book.home.createOrEditLabel'));
  saveButton: ElementFinder = element(by.id('save-entity'));
  cancelButton: ElementFinder = element(by.id('cancel-save'));
  isbnInput: ElementFinder = element(by.css('input#book-isbn'));
  nameInput: ElementFinder = element(by.css('input#book-name'));
  publishYearInput: ElementFinder = element(by.css('input#book-publishYear'));
  copiesInput: ElementFinder = element(by.css('input#book-copies'));
  coverInput: ElementFinder = element(by.css('input#file_cover'));
  publisherSelect: ElementFinder = element(by.css('select#book-publisher'));
  authorSelect: ElementFinder = element(by.css('select#book-author'));

  getPageTitle() {
    return this.pageTitle;
  }

  async setIsbnInput(isbn) {
    await this.isbnInput.sendKeys(isbn);
  }

  async getIsbnInput() {
    return this.isbnInput.getAttribute('value');
  }

  async setNameInput(name) {
    await this.nameInput.sendKeys(name);
  }

  async getNameInput() {
    return this.nameInput.getAttribute('value');
  }

  async setPublishYearInput(publishYear) {
    await this.publishYearInput.sendKeys(publishYear);
  }

  async getPublishYearInput() {
    return this.publishYearInput.getAttribute('value');
  }

  async setCopiesInput(copies) {
    await this.copiesInput.sendKeys(copies);
  }

  async getCopiesInput() {
    return this.copiesInput.getAttribute('value');
  }

  async setCoverInput(cover) {
    await this.coverInput.sendKeys(cover);
  }

  async getCoverInput() {
    return this.coverInput.getAttribute('value');
  }

  async publisherSelectLastOption() {
    await this.publisherSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async publisherSelectOption(option) {
    await this.publisherSelect.sendKeys(option);
  }

  getPublisherSelect() {
    return this.publisherSelect;
  }

  async getPublisherSelectedOption() {
    return this.publisherSelect.element(by.css('option:checked')).getText();
  }

  async authorSelectLastOption() {
    await this.authorSelect
      .all(by.tagName('option'))
      .last()
      .click();
  }

  async authorSelectOption(option) {
    await this.authorSelect.sendKeys(option);
  }

  getAuthorSelect() {
    return this.authorSelect;
  }

  async getAuthorSelectedOption() {
    return this.authorSelect.element(by.css('option:checked')).getText();
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
