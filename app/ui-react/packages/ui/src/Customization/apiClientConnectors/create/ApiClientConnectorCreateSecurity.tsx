import {
  Card,
  ControlLabel,
  FormControl,
  FormGroup,
  Radio,
} from 'patternfly-react';
import * as React from 'react';
import { ButtonLink } from '../../../Layout';

export interface IAuthenticationType {
  label: string;
  value: 'basic' | 'apiKey' | 'oauth2';
}

export interface IApiClientConnectorCreateSecurityProps {
  /**
   * Access token, required for OAuth 2.0.
   */
  accessToken?: string;
  /**
   * The type of authentication.
   * Optional, though required in OpenAPI 2.0 for the security scheme object.
   * Valid values are "basic", "apiKey" or "oauth2".
   */
  authenticationType?: IAuthenticationType[];
  /**
   * Authorization URL, required for OAuth 2.0.
   */
  authorizationUrl?: string;
  i18nAccessTokenUrl: string;
  i18nAuthorizationUrl: string;
  i18nBtnNext: string;
  /**
   * Locale string for when no security is specified
   */
  i18nNoSecurity: string;
  i18nTitle: string;

  /**
   * The action fired when the user presses the Next button
   */
  onNext(
    accessToken?: string,
    authenticationType?: IAuthenticationType[],
    authorizationUrl?: string
  ): void;
}

export interface IApiClientConnectorCreateSecurityState {
  accessTokenUrl?: string;
  authorizationUrl?: string;
  selectedType?: IAuthenticationType;
  valid: boolean;
}

export class ApiClientConnectorCreateSecurity extends React.Component<
  IApiClientConnectorCreateSecurityProps,
  IApiClientConnectorCreateSecurityState
> {
  constructor(props: any) {
    super(props);
    /**
     * TODO: Improve this, crappy
     */
    this.state = {
      selectedType: {
        label: 'HTTP Basic Authentication',
        value: 'basic',
      },
      valid: false,
    };

    this.onSelectType = this.onSelectType.bind(this);
    this.setAccessTokenUrl = this.setAccessTokenUrl.bind(this);
    this.setAuthorizationUrl = this.setAuthorizationUrl.bind(this);
  }

  /**
   * The action fired when the user selects the authentication
   * type they want to use for the client connector.
   * @param newType
   */
  public onSelectType(newType: IAuthenticationType) {
    this.setState({
      selectedType: newType,
      valid: newType.value === 'basic',
    });
  }

  public setAccessTokenUrl(e: React.FormEvent<HTMLInputElement>) {
    this.setState({ accessTokenUrl: e.currentTarget.value });
  }

  public setAuthorizationUrl(e: React.FormEvent<HTMLInputElement>) {
    this.setState({ authorizationUrl: e.currentTarget.value });
  }

  public render() {
    return (
      <Card style={{ maxWidth: '600px' }}>
        <Card.Heading>
          <Card.Title>{this.props.i18nTitle}</Card.Title>
        </Card.Heading>
        <Card.Body>
          <FormGroup controlId={'authenticationType'} disabled={false}>
            {this.props.authenticationType!.map(
              (authType: IAuthenticationType, idx) => {
                return (
                  <div key={authType.value}>
                    <Radio
                      id={'authenticationType'}
                      aria-label={'Authentication Type'}
                      checked={
                        this.state.selectedType!.value === authType.value
                      }
                      name={'authenticationType'}
                      onClick={() => this.onSelectType(authType)}
                      readOnly={true}
                    >
                      {authType.label || this.props.i18nNoSecurity}
                    </Radio>
                  </div>
                );
              }
            )}
            {this.state.selectedType &&
              this.state.selectedType.value === 'oauth2' && (
                <>
                  <FormGroup controlId={'authorizationUrl'} disabled={false}>
                    <ControlLabel>
                      {this.props.i18nAuthorizationUrl}
                    </ControlLabel>
                    <FormControl
                      type={'text'}
                      value={
                        this.state.authorizationUrl ||
                        this.props.authorizationUrl
                      }
                      onChange={this.setAuthorizationUrl}
                    />
                  </FormGroup>
                  <FormGroup controlId={'accessTokenUrl'} disabled={false}>
                    <ControlLabel>{this.props.i18nAccessTokenUrl}</ControlLabel>
                    <FormControl
                      type={'text'}
                      value={
                        this.state.accessTokenUrl || this.props.accessToken
                      }
                      onChange={this.setAccessTokenUrl}
                    />
                  </FormGroup>
                </>
              )}
          </FormGroup>
          <ButtonLink onClick={this.props.onNext} as={'primary'}>
            {this.props.i18nBtnNext}
          </ButtonLink>
        </Card.Body>
      </Card>
    );
  }
}
