export interface ResetPasswordState {
  email?: string;
  token?: string;
  otp?: string;
  password?: string;
}

export const initialState: ResetPasswordState = {
  email: null,
  token: null,
  otp: null,
  password: null,
};
