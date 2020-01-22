import { browser, element, by } from 'protractor';

import NavBarPage from './../../page-objects/navbar-page';
import SignInPage from './../../page-objects/signin-page';
import BookComponentsPage, { BookDeleteDialog } from './book.page-object';
import BookUpdatePage from './book-update.page-object';
import { waitUntilDisplayed, waitUntilHidden } from '../../util/utils';
import path from 'path';

const expect = chai.expect;

describe('Book e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let bookComponentsPage: BookComponentsPage;
  let bookUpdatePage: BookUpdatePage;
  let bookDeleteDialog: BookDeleteDialog;
  const fileToUpload = '../../../../../../src/main/webapp/content/images/logo-jhipster.png';
  const absolutePath = path.resolve(__dirname, fileToUpload);

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.waitUntilDisplayed();

    await signInPage.username.sendKeys('admin');
    await signInPage.password.sendKeys('admin');
    await signInPage.loginButton.click();
    await signInPage.waitUntilHidden();
    await waitUntilDisplayed(navBarPage.entityMenu);
    await waitUntilDisplayed(navBarPage.adminMenu);
    await waitUntilDisplayed(navBarPage.accountMenu);
  });

  it('should load Books', async () => {
    await navBarPage.getEntityPage('book');
    bookComponentsPage = new BookComponentsPage();
    expect(await bookComponentsPage.getTitle().getText()).to.match(/Books/);
  });

  it('should load create Book page', async () => {
    await bookComponentsPage.clickOnCreateButton();
    bookUpdatePage = new BookUpdatePage();
    expect(await bookUpdatePage.getPageTitle().getText()).to.match(/Create or edit a Book/);
    await bookUpdatePage.cancel();
  });

  it('should create and save Books', async () => {
    async function createBook() {
      await bookComponentsPage.clickOnCreateButton();
      await bookUpdatePage.setIsbnInput('isbn');
      expect(await bookUpdatePage.getIsbnInput()).to.match(/isbn/);
      await bookUpdatePage.setNameInput('name');
      expect(await bookUpdatePage.getNameInput()).to.match(/name/);
      await bookUpdatePage.setPublishYearInput('publishYear');
      expect(await bookUpdatePage.getPublishYearInput()).to.match(/publishYear/);
      await bookUpdatePage.setCopiesInput('5');
      expect(await bookUpdatePage.getCopiesInput()).to.eq('5');
      await bookUpdatePage.setCoverInput(absolutePath);
      await bookUpdatePage.publisherSelectLastOption();
      // bookUpdatePage.authorSelectLastOption();
      await waitUntilDisplayed(bookUpdatePage.getSaveButton());
      await bookUpdatePage.save();
      await waitUntilHidden(bookUpdatePage.getSaveButton());
      expect(await bookUpdatePage.getSaveButton().isPresent()).to.be.false;
    }

    await createBook();
    await bookComponentsPage.waitUntilLoaded();
    const nbButtonsBeforeCreate = await bookComponentsPage.countDeleteButtons();
    await createBook();
    await bookComponentsPage.waitUntilLoaded();

    await bookComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeCreate + 1);
    expect(await bookComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1);
  });

  it('should delete last Book', async () => {
    await bookComponentsPage.waitUntilLoaded();
    const nbButtonsBeforeDelete = await bookComponentsPage.countDeleteButtons();
    await bookComponentsPage.clickOnLastDeleteButton();

    const deleteModal = element(by.className('modal'));
    await waitUntilDisplayed(deleteModal);

    bookDeleteDialog = new BookDeleteDialog();
    expect(await bookDeleteDialog.getDialogTitle().getAttribute('id')).to.match(/libraryApp.book.delete.question/);
    await bookDeleteDialog.clickOnConfirmButton();

    await bookComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeDelete - 1);
    expect(await bookComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
