import { browser, element, by } from 'protractor';

import NavBarPage from './../../page-objects/navbar-page';
import SignInPage from './../../page-objects/signin-page';
import BorrowedBookComponentsPage, { BorrowedBookDeleteDialog } from './borrowed-book.page-object';
import BorrowedBookUpdatePage from './borrowed-book-update.page-object';
import { waitUntilDisplayed, waitUntilHidden } from '../../util/utils';

const expect = chai.expect;

describe('BorrowedBook e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let borrowedBookComponentsPage: BorrowedBookComponentsPage;
  let borrowedBookUpdatePage: BorrowedBookUpdatePage;
  let borrowedBookDeleteDialog: BorrowedBookDeleteDialog;

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

  it('should load BorrowedBooks', async () => {
    await navBarPage.getEntityPage('borrowed-book');
    borrowedBookComponentsPage = new BorrowedBookComponentsPage();
    expect(await borrowedBookComponentsPage.getTitle().getText()).to.match(/Borrowed Books/);
  });

  it('should load create BorrowedBook page', async () => {
    await borrowedBookComponentsPage.clickOnCreateButton();
    borrowedBookUpdatePage = new BorrowedBookUpdatePage();
    expect(await borrowedBookUpdatePage.getPageTitle().getText()).to.match(/Create or edit a BorrowedBook/);
    await borrowedBookUpdatePage.cancel();
  });

  it('should create and save BorrowedBooks', async () => {
    async function createBorrowedBook() {
      await borrowedBookComponentsPage.clickOnCreateButton();
      await borrowedBookUpdatePage.setBorrowDateInput('01-01-2001');
      expect(await borrowedBookUpdatePage.getBorrowDateInput()).to.eq('2001-01-01');
      await borrowedBookUpdatePage.bookSelectLastOption();
      await borrowedBookUpdatePage.clientSelectLastOption();
      await waitUntilDisplayed(borrowedBookUpdatePage.getSaveButton());
      await borrowedBookUpdatePage.save();
      await waitUntilHidden(borrowedBookUpdatePage.getSaveButton());
      expect(await borrowedBookUpdatePage.getSaveButton().isPresent()).to.be.false;
    }

    await createBorrowedBook();
    await borrowedBookComponentsPage.waitUntilLoaded();
    const nbButtonsBeforeCreate = await borrowedBookComponentsPage.countDeleteButtons();
    await createBorrowedBook();
    await borrowedBookComponentsPage.waitUntilLoaded();

    await borrowedBookComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeCreate + 1);
    expect(await borrowedBookComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1);
  });

  it('should delete last BorrowedBook', async () => {
    await borrowedBookComponentsPage.waitUntilLoaded();
    const nbButtonsBeforeDelete = await borrowedBookComponentsPage.countDeleteButtons();
    await borrowedBookComponentsPage.clickOnLastDeleteButton();

    const deleteModal = element(by.className('modal'));
    await waitUntilDisplayed(deleteModal);

    borrowedBookDeleteDialog = new BorrowedBookDeleteDialog();
    expect(await borrowedBookDeleteDialog.getDialogTitle().getAttribute('id')).to.match(/libraryApp.borrowedBook.delete.question/);
    await borrowedBookDeleteDialog.clickOnConfirmButton();

    await borrowedBookComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeDelete - 1);
    expect(await borrowedBookComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
