import { browser, element, by } from 'protractor';

import NavBarPage from './../../page-objects/navbar-page';
import SignInPage from './../../page-objects/signin-page';
import ClientComponentsPage, { ClientDeleteDialog } from './client.page-object';
import ClientUpdatePage from './client-update.page-object';
import { waitUntilDisplayed, waitUntilHidden } from '../../util/utils';

const expect = chai.expect;

describe('Client e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let clientComponentsPage: ClientComponentsPage;
  let clientUpdatePage: ClientUpdatePage;
  let clientDeleteDialog: ClientDeleteDialog;

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

  it('should load Clients', async () => {
    await navBarPage.getEntityPage('client');
    clientComponentsPage = new ClientComponentsPage();
    expect(await clientComponentsPage.getTitle().getText()).to.match(/Clients/);
  });

  it('should load create Client page', async () => {
    await clientComponentsPage.clickOnCreateButton();
    clientUpdatePage = new ClientUpdatePage();
    expect(await clientUpdatePage.getPageTitle().getText()).to.match(/Create or edit a Client/);
    await clientUpdatePage.cancel();
  });

  it('should create and save Clients', async () => {
    async function createClient() {
      await clientComponentsPage.clickOnCreateButton();
      await clientUpdatePage.setFirstNameInput('firstName');
      expect(await clientUpdatePage.getFirstNameInput()).to.match(/firstName/);
      await clientUpdatePage.setLastNameInput('lastName');
      expect(await clientUpdatePage.getLastNameInput()).to.match(/lastName/);
      await clientUpdatePage.setEmailInput('email');
      expect(await clientUpdatePage.getEmailInput()).to.match(/email/);
      await clientUpdatePage.setAddressInput('address');
      expect(await clientUpdatePage.getAddressInput()).to.match(/address/);
      await clientUpdatePage.setPhoneInput('phone');
      expect(await clientUpdatePage.getPhoneInput()).to.match(/phone/);
      await waitUntilDisplayed(clientUpdatePage.getSaveButton());
      await clientUpdatePage.save();
      await waitUntilHidden(clientUpdatePage.getSaveButton());
      expect(await clientUpdatePage.getSaveButton().isPresent()).to.be.false;
    }

    await createClient();
    await clientComponentsPage.waitUntilLoaded();
    const nbButtonsBeforeCreate = await clientComponentsPage.countDeleteButtons();
    await createClient();
    await clientComponentsPage.waitUntilLoaded();

    await clientComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeCreate + 1);
    expect(await clientComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1);
  });

  it('should delete last Client', async () => {
    await clientComponentsPage.waitUntilLoaded();
    const nbButtonsBeforeDelete = await clientComponentsPage.countDeleteButtons();
    await clientComponentsPage.clickOnLastDeleteButton();

    const deleteModal = element(by.className('modal'));
    await waitUntilDisplayed(deleteModal);

    clientDeleteDialog = new ClientDeleteDialog();
    expect(await clientDeleteDialog.getDialogTitle().getAttribute('id')).to.match(/libraryApp.client.delete.question/);
    await clientDeleteDialog.clickOnConfirmButton();

    await clientComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeDelete - 1);
    expect(await clientComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
