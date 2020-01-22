import { browser, element, by } from 'protractor';

import NavBarPage from './../../page-objects/navbar-page';
import SignInPage from './../../page-objects/signin-page';
import AuthorComponentsPage, { AuthorDeleteDialog } from './author.page-object';
import AuthorUpdatePage from './author-update.page-object';
import { waitUntilDisplayed, waitUntilHidden } from '../../util/utils';

const expect = chai.expect;

describe('Author e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let authorComponentsPage: AuthorComponentsPage;
  let authorUpdatePage: AuthorUpdatePage;
  let authorDeleteDialog: AuthorDeleteDialog;

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

  it('should load Authors', async () => {
    await navBarPage.getEntityPage('author');
    authorComponentsPage = new AuthorComponentsPage();
    expect(await authorComponentsPage.getTitle().getText()).to.match(/Authors/);
  });

  it('should load create Author page', async () => {
    await authorComponentsPage.clickOnCreateButton();
    authorUpdatePage = new AuthorUpdatePage();
    expect(await authorUpdatePage.getPageTitle().getText()).to.match(/Create or edit a Author/);
    await authorUpdatePage.cancel();
  });

  it('should create and save Authors', async () => {
    async function createAuthor() {
      await authorComponentsPage.clickOnCreateButton();
      await authorUpdatePage.setFirstNameInput('firstName');
      expect(await authorUpdatePage.getFirstNameInput()).to.match(/firstName/);
      await authorUpdatePage.setLastNameInput('lastName');
      expect(await authorUpdatePage.getLastNameInput()).to.match(/lastName/);
      await waitUntilDisplayed(authorUpdatePage.getSaveButton());
      await authorUpdatePage.save();
      await waitUntilHidden(authorUpdatePage.getSaveButton());
      expect(await authorUpdatePage.getSaveButton().isPresent()).to.be.false;
    }

    await createAuthor();
    await authorComponentsPage.waitUntilLoaded();
    const nbButtonsBeforeCreate = await authorComponentsPage.countDeleteButtons();
    await createAuthor();
    await authorComponentsPage.waitUntilLoaded();

    await authorComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeCreate + 1);
    expect(await authorComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1);
  });

  it('should delete last Author', async () => {
    await authorComponentsPage.waitUntilLoaded();
    const nbButtonsBeforeDelete = await authorComponentsPage.countDeleteButtons();
    await authorComponentsPage.clickOnLastDeleteButton();

    const deleteModal = element(by.className('modal'));
    await waitUntilDisplayed(deleteModal);

    authorDeleteDialog = new AuthorDeleteDialog();
    expect(await authorDeleteDialog.getDialogTitle().getAttribute('id')).to.match(/libraryApp.author.delete.question/);
    await authorDeleteDialog.clickOnConfirmButton();

    await authorComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeDelete - 1);
    expect(await authorComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
