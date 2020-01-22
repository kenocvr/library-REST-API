import { browser, element, by } from 'protractor';

import NavBarPage from './../../page-objects/navbar-page';
import SignInPage from './../../page-objects/signin-page';
import PublisherComponentsPage, { PublisherDeleteDialog } from './publisher.page-object';
import PublisherUpdatePage from './publisher-update.page-object';
import { waitUntilDisplayed, waitUntilHidden } from '../../util/utils';

const expect = chai.expect;

describe('Publisher e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let publisherComponentsPage: PublisherComponentsPage;
  let publisherUpdatePage: PublisherUpdatePage;
  let publisherDeleteDialog: PublisherDeleteDialog;

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

  it('should load Publishers', async () => {
    await navBarPage.getEntityPage('publisher');
    publisherComponentsPage = new PublisherComponentsPage();
    expect(await publisherComponentsPage.getTitle().getText()).to.match(/Publishers/);
  });

  it('should load create Publisher page', async () => {
    await publisherComponentsPage.clickOnCreateButton();
    publisherUpdatePage = new PublisherUpdatePage();
    expect(await publisherUpdatePage.getPageTitle().getText()).to.match(/Create or edit a Publisher/);
    await publisherUpdatePage.cancel();
  });

  it('should create and save Publishers', async () => {
    async function createPublisher() {
      await publisherComponentsPage.clickOnCreateButton();
      await publisherUpdatePage.setNameInput('name');
      expect(await publisherUpdatePage.getNameInput()).to.match(/name/);
      await waitUntilDisplayed(publisherUpdatePage.getSaveButton());
      await publisherUpdatePage.save();
      await waitUntilHidden(publisherUpdatePage.getSaveButton());
      expect(await publisherUpdatePage.getSaveButton().isPresent()).to.be.false;
    }

    await createPublisher();
    await publisherComponentsPage.waitUntilLoaded();
    const nbButtonsBeforeCreate = await publisherComponentsPage.countDeleteButtons();
    await createPublisher();
    await publisherComponentsPage.waitUntilLoaded();

    await publisherComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeCreate + 1);
    expect(await publisherComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1);
  });

  it('should delete last Publisher', async () => {
    await publisherComponentsPage.waitUntilLoaded();
    const nbButtonsBeforeDelete = await publisherComponentsPage.countDeleteButtons();
    await publisherComponentsPage.clickOnLastDeleteButton();

    const deleteModal = element(by.className('modal'));
    await waitUntilDisplayed(deleteModal);

    publisherDeleteDialog = new PublisherDeleteDialog();
    expect(await publisherDeleteDialog.getDialogTitle().getAttribute('id')).to.match(/libraryApp.publisher.delete.question/);
    await publisherDeleteDialog.clickOnConfirmButton();

    await publisherComponentsPage.waitUntilDeleteButtonsLength(nbButtonsBeforeDelete - 1);
    expect(await publisherComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
